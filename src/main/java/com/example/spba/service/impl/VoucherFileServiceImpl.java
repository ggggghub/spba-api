package com.example.spba.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.spba.dao.VoucherFileMapper;
import com.example.spba.domain.dto.AttachmentStatusResp;
import com.example.spba.domain.dto.UploadResultDTO;
import com.example.spba.domain.entity.VoucherFile;
import com.example.spba.service.VoucherFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherFileServiceImpl extends ServiceImpl<VoucherFileMapper, VoucherFile>
        implements VoucherFileService {

    @Value("${app.upload.base-dir}")
    private String baseDir;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UploadResultDTO upload(MultipartFile file, String identityNumber, String indexNo, String voucherNo) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        try (InputStream in = file.getInputStream()) {
            String year = String.valueOf(LocalDate.now().getYear());
            Path dir = Paths.get(baseDir, year + "_" + identityNumber);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // 并发安全：行锁方式获取最大 seq
            Integer maxSeq = baseMapper.selectMaxSeqForUpdate(identityNumber, indexNo);
            int nextSeq = (maxSeq == null ? 0 : maxSeq) + 1;

            // Hutool 获取扩展名
            String ext = FileUtil.extName(file.getOriginalFilename());
            // 拼接保存名
            String saveName = indexNo + "-" + nextSeq + (StrUtil.isNotBlank(ext) ? "." + ext : "");
            Path savePath = dir.resolve(saveName);

            Files.copy(in, savePath, StandardCopyOption.REPLACE_EXISTING);

            VoucherFile entity = new VoucherFile();
            entity.setIdentityNumber(identityNumber);
            entity.setIndexNo(indexNo);
            entity.setVoucherNo(voucherNo);
            entity.setSeq(nextSeq);
            entity.setFileName(saveName);
            entity.setFilePath(savePath.toString());
            entity.setExt(ext);
            entity.setUploadDate(LocalDateTime.now());
            save(entity);

            UploadResultDTO dto = new UploadResultDTO();
            dto.setIndexNo(indexNo);
            dto.setSeq(nextSeq);
            dto.setFileName(saveName);
            dto.setFilePath(savePath.toString());
            dto.setExt(ext);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("保存文件失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<VoucherFile> listByIndex(String identityNumber, String indexNo) {
        return lambdaQuery()
                .eq(VoucherFile::getIdentityNumber, identityNumber)
                .eq(VoucherFile::getIndexNo, indexNo)
                .orderByAsc(VoucherFile::getSeq)
                .list();
    }

    @Override
    public boolean removeOne(Long id) {
        VoucherFile vf = getById(id);
        if (vf != null) {
            try {
                Files.deleteIfExists(Paths.get(vf.getFilePath()));
            } catch (Exception ignore) {}
            return removeById(id);
        }
        return true;
    }



    @Override
    public List<AttachmentStatusResp> status(String identityNumber, String indexNo, List<String> voucherNos) {
        if (voucherNos == null || voucherNos.isEmpty()) return Collections.emptyList();
        Set<String> wanted = voucherNos.stream().filter(Objects::nonNull).map(String::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<VoucherFile> list = lambdaQuery()
                .eq(VoucherFile::getIdentityNumber, identityNumber)
                .eq(VoucherFile::getIndexNo, indexNo)
                .in(VoucherFile::getVoucherNo, wanted)
                .eq(VoucherFile::getDeleted, 0)
                .orderByDesc(VoucherFile::getSeq)
                .list();

        Map<String, VoucherFile> latest = new LinkedHashMap<>();
        for (VoucherFile v : list) {
            latest.putIfAbsent(String.valueOf(v.getVoucherNo()), v); // 第一条即最新
        }

        List<AttachmentStatusResp> resp = new ArrayList<>();
        for (String vn : wanted) {
            VoucherFile hit = latest.get(vn);
            if (hit != null) {
                resp.add(new AttachmentStatusResp(vn, true, hit.getId(), hit.getFileName(), hit.getFilePath(), hit.getValidStatus()));
            } else {
                resp.add(new AttachmentStatusResp(vn, false, null, null, null, null));
            }
        }
        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean verifyLatest(String identityNumber, String indexNo, String voucherNo, Integer validStatus, String remark, String operator) {
        VoucherFile latest = lambdaQuery()
                .eq(VoucherFile::getIdentityNumber, identityNumber)
                .eq(VoucherFile::getIndexNo, indexNo)
                .eq(VoucherFile::getVoucherNo, voucherNo)
                .eq(VoucherFile::getDeleted, 0)
                .orderByDesc(VoucherFile::getSeq)
                .last("LIMIT 1").one();

        if (latest == null) return false;

        latest.setValidStatus(validStatus);
        latest.setRemark(remark);
        latest.setVerifiedBy(operator);
        latest.setVerifiedAt(LocalDateTime.now());
        return updateById(latest);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean adminDelete(Long id, String operator) {
        VoucherFile vf = getById(id);
        if (vf == null || Objects.equals(vf.getDeleted(), 1)) return true;

        // 物理删除文件
        try { Files.deleteIfExists(Paths.get(vf.getFilePath())); } catch (Exception ignore) {}

        // 软删记录
        vf.setDeleted(1);
        vf.setDeletedAt(LocalDateTime.now());
        vf.setDeletedBy(operator);
        return updateById(vf);
    }
}
