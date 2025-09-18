package com.example.spba.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditVoucherFile {
    private Integer id;
    private String identityNumber;  // 企业识别号
    private String voucherNo;       // 凭证编号
    private String fileName;        // 原始文件名
    private String storedName;      // HQ-5-文件名
    private String filePath;        // 磁盘路径
    private String fileUrl;         // 访问 URL
    private String ext;             // 扩展名
    private LocalDateTime uploadDate;
    private Integer status;
    private String reviewComment;
}