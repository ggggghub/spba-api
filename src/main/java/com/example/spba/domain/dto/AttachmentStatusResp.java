package com.example.spba.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 响应：每个 voucherNo 的状态
@Data @AllArgsConstructor @NoArgsConstructor
public class AttachmentStatusResp {
    private String voucherNo;
    private Boolean uploaded;
    private Long fileId;         // 最新文件ID
    private String fileName;     // 最新文件名
    private String filePath;     // 最新文件路径（管理员要显示）
    private Integer validStatus; // 1/0/null
}