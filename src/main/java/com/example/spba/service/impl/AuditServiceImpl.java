package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.dao.AuditRecordMapper;
import com.example.spba.dao.AuditSheetMapper;
import com.example.spba.domain.entity.AuditRecord;
import com.example.spba.domain.entity.AuditSheet;
import com.example.spba.service.AuditService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
public class AuditServiceImpl extends ServiceImpl<AuditRecordMapper, AuditRecord> implements AuditService {

    private final AuditSheetMapper sheetMapper;

    @Value("${app.upload-root}")
    private String uploadRoot;

    public AuditServiceImpl(AuditSheetMapper sheetMapper) {
        this.sheetMapper = sheetMapper;
    }

    /** 工具：保存文件并返回可直接访问的 URL（/files/**） */
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        String dateDir = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now());
        File dir = new File(uploadRoot, subDir + "/" + dateDir);
        if (!dir.exists()) dir.mkdirs();
        String filename = UUID.randomUUID() + "_" + Objects.requireNonNull(file.getOriginalFilename());
        File dest = new File(dir, filename);
        file.transferTo(dest);
        // 让前端直接 window.open 即可
        String relPath = "/files/" + subDir + "/" + dateDir + "/" + filename;
        return relPath.replace("\\", "/");
    }

    @Override
    public R sheetState(String identityNumber) {
        LambdaQueryWrapper<AuditSheet> qw = new LambdaQueryWrapper<AuditSheet>()
                .eq(AuditSheet::getIdentityNumber, identityNumber)
                .eq(AuditSheet::getUploadedBy, "ADMIN")
                .orderByDesc(AuditSheet::getId).last("limit 1");
        AuditSheet sheet = sheetMapper.selectOne(qw);
        Map<String, Object> data = new HashMap<>();
        data.put("hasAdminSheet", sheet != null);
        data.put("adminSheetUrl", sheet != null ? sheet.getFileUrl() : "");
        return R.success(data);
    }

    @Override
    public R listByIdentity(String identityNumber, boolean isAdmin) {
        // 取该 identity 最新的 ADMIN 基础表（如果没有，就查全部该identity的记录）
        LambdaQueryWrapper<AuditSheet> sheetQ = new LambdaQueryWrapper<AuditSheet>()
                .eq(AuditSheet::getIdentityNumber, identityNumber)
                .eq(AuditSheet::getUploadedBy, "ADMIN")
                .orderByDesc(AuditSheet::getId).last("limit 1");

        AuditSheet adminSheet = sheetMapper.selectOne(sheetQ);
        LambdaQueryWrapper<AuditRecord> recQ = new LambdaQueryWrapper<AuditRecord>()
                .eq(AuditRecord::getIdentityNumber, identityNumber);
        if (adminSheet != null) recQ.eq(AuditRecord::getSheetId, adminSheet.getId());
        recQ.orderByDesc(AuditRecord::getId);

        List<AuditRecord> rows = this.list(recQ);

        // 客户端需要隐藏 conclusion 字段可在前端控制，这里统一返回
        Map<String, Object> resp = new HashMap<>();
        resp.put("rows", rows);
        resp.put("hasAdminSheet", adminSheet != null);
        resp.put("adminSheetUrl", adminSheet != null ? adminSheet.getFileUrl() : "");
        return R.success(resp);
    }

    @Override
    public R uploadAdminBaseSheet(String identityNumber, MultipartFile excel) {
        // 若已存在管理员基础表，不允许重复创建（根据你的需求）
        LambdaQueryWrapper<AuditSheet> qw = new LambdaQueryWrapper<AuditSheet>()
                .eq(AuditSheet::getIdentityNumber, identityNumber)
                .eq(AuditSheet::getUploadedBy, "ADMIN");
        if (sheetMapper.selectCount(qw) > 0) {
            return R.error("该用户已存在管理员基础表，如需更新请联系运维或走替换流程。");
        }

        try {
            String url = saveFile(excel, "base");
            AuditSheet sheet = new AuditSheet();
            sheet.setIdentityNumber(identityNumber);
            sheet.setUploadedBy("ADMIN");
            sheet.setFileUrl(url);
            sheetMapper.insert(sheet);

            // 解析 Excel → 生成多行 AuditRecord（此处给出示例伪解析，你可替换为 Apache POI 实现）
            // demo: 这里插入 3 行空白记录，真实项目请解析 excel 行填充各字段
            for (int i = 0; i < 3; i++) {
                AuditRecord r = new AuditRecord();
                r.setSheetId(sheet.getId());
                r.setIdentityNumber(identityNumber);
                r.setBusinessContent("根据选择后账套录入" + (i + 1));
                r.setRemark("可确认/需问题后确认");
                this.save(r);
            }

            return R.success(sheet, "基础表上传并初始化成功");
        } catch (IOException e) {
            return R.error("上传失败：" + e.getMessage());
        }
    }

    @Override
    public R uploadRowAttachment(Long recordId, MultipartFile file) {
        AuditRecord record = this.getById(recordId);
        if (record == null) return R.error("记录不存在");

        try {
            String url = saveFile(file, "attachments");
            record.setAttachmentUrl(url);
            // 自动生成索引号
            record.setIndexNo("IDX-" + System.currentTimeMillis());
            this.updateById(record);
            return R.success(record, "附件上传成功");
        } catch (IOException e) {
            return R.error("附件上传失败：" + e.getMessage());
        }
    }

    @Override
    public R updateConclusion(Long recordId, String conclusion) {
        if (!"通过".equals(conclusion) && !"不通过".equals(conclusion)) {
            return R.error("结论只能是【通过/不通过】");
        }
        AuditRecord record = this.getById(recordId);
        if (record == null) return R.error("记录不存在");
        record.setConclusion(conclusion);
        this.updateById(record);
        return R.success(record, "结论更新成功");
    }

    @Override
    public R saveOrUpdateRows(String identityNumber, List<AuditRecord> rows) {
        if (rows == null || rows.isEmpty()) return R.success("无变更");
        for (AuditRecord r : rows) {
            r.setIdentityNumber(identityNumber);
        }
        this.saveOrUpdateBatch(rows);
        return R.success("保存成功");
    }
}