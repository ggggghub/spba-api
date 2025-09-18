package com.example.spba.service;

import com.example.spba.domain.entity.AuditVoucherFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoucherUploadService {

    /**
     * 上传文件并保存到本地+数据库
     */
    List<AuditVoucherFile> uploadFiles(String identityNumber, String voucherNo, MultipartFile[] files);

    /**
     * 查询某个凭证下的所有附件
     */
    List<AuditVoucherFile> getFiles(String identityNumber, String voucherNo);

    /**
     * 查询某个企业的所有附件
     */
    List<AuditVoucherFile> getFilesByIdentityNumber(String identityNumber);
}
