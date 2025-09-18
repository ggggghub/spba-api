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
import java.util.function.Function;
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

        // 先把请求的 voucherNo 做成 set 提高匹配效率
        Set<String> wanted = voucherNos.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new)); // 保持顺序

        // 查出这些 voucherNo 的所有记录，按 seq 降序（第一条就是最新）
        List<VoucherFile> list = lambdaQuery()
                .eq(VoucherFile::getIdentityNumber, identityNumber)
                .eq(VoucherFile::getIndexNo, indexNo)
                .in(VoucherFile::getVoucherNo, wanted)
                .orderByDesc(VoucherFile::getSeq)
                .list();

        // 取每个 voucherNo 的第一条（最新）
        Map<String, VoucherFile> latestByVoucher =
                list.stream()
                        .collect(Collectors.toMap(
                                v -> String.valueOf(v.getVoucherNo()),
                                Function.identity(),
                                (a, b) -> a)); // 因为已经按 seq DESC，第一次出现即最新

        // 组装响应，保持与请求顺序一致
        List<AttachmentStatusResp> resp = new ArrayList<>();
        for (String vn : wanted) {
            VoucherFile hit = latestByVoucher.get(vn);
            if (hit != null) {
                resp.add(new AttachmentStatusResp(vn, true, hit.getFileName()));
            } else {
                resp.add(new AttachmentStatusResp(vn, false, null));
            }
        }
        return resp;
    }
}
