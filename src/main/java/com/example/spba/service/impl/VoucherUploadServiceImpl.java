package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spba.domain.entity.AuditTask;
import com.example.spba.domain.entity.AuditVoucherFile;
import com.example.spba.domain.enums.ReviewStatus;
import com.example.spba.dao.AuditTaskMapper;
import com.example.spba.dao.AuditVoucherFileMapper;
import com.example.spba.service.VoucherUploadService;
import com.example.spba.utils.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VoucherUploadServiceImpl implements VoucherUploadService {

    @Resource
    private AuditTaskMapper auditTaskMapper;

    @Resource
    private AuditVoucherFileMapper auditVoucherFileMapper;

    @Override
    public List<AuditVoucherFile> uploadFiles(Integer taskId, String voucherNo, String uploader, MultipartFile[] files) {
        AuditTask task = auditTaskMapper.selectById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("任务不存在");
        }

        Path root = Paths.get("D:/workSpace/audit/customers");
        Path dir = FileUtils.resolveVoucherDir(root, task.getCompanyName(), LocalDate.now(), voucherNo);
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            throw new RuntimeException("创建凭证目录失败", e);
        }

        List<AuditVoucherFile> result = new ArrayList<>();
        for (MultipartFile f : files) {
            try {
                String ext = FilenameUtils.getExtension(f.getOriginalFilename());
                String safeName = System.currentTimeMillis() + "_" + FileUtils.sanitize(f.getOriginalFilename());
                Path dest = dir.resolve(safeName);
                f.transferTo(dest.toFile());

                AuditVoucherFile rec = new AuditVoucherFile();
                rec.setTaskId(taskId);
                rec.setVoucherNo(voucherNo);
                rec.setFileName(f.getOriginalFilename());
                rec.setFilePath(dest.toString());
                rec.setExt(ext.toLowerCase());
                rec.setUploadBy(uploader);
                rec.setUploadDate(new Date());
                rec.setStatus(ReviewStatus.PENDING.getCode());
                auditVoucherFileMapper.insert(rec);

                result.add(rec);
            } catch (Exception e) {
                throw new RuntimeException("文件上传失败: " + f.getOriginalFilename(), e);
            }
        }
        return result;
    }

    @Override
    public List<AuditVoucherFile> getFiles(Integer taskId, String voucherNo) {
        return auditVoucherFileMapper.selectList(
            new LambdaQueryWrapper<AuditVoucherFile>()
                .eq(AuditVoucherFile::getTaskId, taskId)
                .eq(AuditVoucherFile::getVoucherNo, voucherNo)
        );
    }
}