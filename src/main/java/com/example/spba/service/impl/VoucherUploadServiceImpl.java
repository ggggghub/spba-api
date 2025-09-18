package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spba.dao.AuditTaskMapper;
import com.example.spba.dao.AuditVoucherFileMapper;
import com.example.spba.domain.entity.AuditTask;
import com.example.spba.domain.entity.AuditVoucherFile;
import com.example.spba.service.VoucherUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoucherUploadServiceImpl implements VoucherUploadService {

    private static final String CUSTOMER_BASE_DIR = "D:/workSpace/audit/customers";

    @Resource
    private AuditTaskMapper taskMapper;

    @Resource
    private AuditVoucherFileMapper fileMapper;

    @Override
    public List<AuditVoucherFile> uploadFiles(String identityNumber, String voucherNo, MultipartFile[] files) {
        List<AuditVoucherFile> savedFiles = new ArrayList<>();

        // 根据 identityNumber 查任务，拿 indexNo
        AuditTask task = taskMapper.selectOne(
            new LambdaQueryWrapper<AuditTask>().eq(AuditTask::getIdentityNumber, identityNumber)
        );
        if (task == null) {
            throw new RuntimeException("未找到任务");
        }
        String indexNo = "";// 需要删除

        // 拼接目录路径
        String year = String.valueOf(LocalDateTime.now().getYear());
        String baseDir = CUSTOMER_BASE_DIR + "/" + year + "_" + identityNumber + "/" + indexNo + "/";
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            if (originalName == null) continue;

            String ext = "";
            int dotIndex = originalName.lastIndexOf(".");
            if (dotIndex > 0) {
                ext = originalName.substring(dotIndex + 1);
            }

            // 存储文件名：HQ-5-原文件名
            String storedName = indexNo + "-" + originalName;

            File dest = new File(dir, storedName);
            try {
                file.transferTo(dest);
            } catch (IOException e) {
                throw new RuntimeException("文件保存失败: " + originalName, e);
            }

            String fileUrl = "/uploads/" + year + "_" + identityNumber + "/" + indexNo + "/" + storedName;

            AuditVoucherFile record = new AuditVoucherFile();
            record.setIdentityNumber(identityNumber);
            record.setVoucherNo(voucherNo);
            record.setFileName(originalName);
            record.setStoredName(storedName);
            record.setFilePath(dest.getAbsolutePath());
            record.setFileUrl(fileUrl);
            record.setExt(ext);
            record.setUploadDate(LocalDateTime.now());
            record.setStatus(0);

            fileMapper.insert(record);
            savedFiles.add(record);
        }
        return savedFiles;
    }

    @Override
    public List<AuditVoucherFile> getFiles(String identityNumber, String voucherNo) {
        return fileMapper.selectList(
            new LambdaQueryWrapper<AuditVoucherFile>()
                .eq(AuditVoucherFile::getIdentityNumber, identityNumber)
                .eq(AuditVoucherFile::getVoucherNo, voucherNo)
                .orderByDesc(AuditVoucherFile::getUploadDate)
        );
    }

    @Override
    public List<AuditVoucherFile> getFilesByIdentityNumber(String identityNumber) {
        return fileMapper.selectList(
            new LambdaQueryWrapper<AuditVoucherFile>()
                .eq(AuditVoucherFile::getIdentityNumber, identityNumber)
                .orderByDesc(AuditVoucherFile::getUploadDate)
        );
    }
}
