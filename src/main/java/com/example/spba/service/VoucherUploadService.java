package com.example.spba.service;

import com.example.spba.domain.entity.AuditVoucherFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoucherUploadService {

    /** 上传一个凭证的多个发票 */
    List<AuditVoucherFile> uploadFiles(Integer taskId, String voucherNo, String uploader, MultipartFile[] files);

    /** 查询某凭证下的上传记录 */
    List<AuditVoucherFile> getFiles(Integer taskId, String voucherNo);
}