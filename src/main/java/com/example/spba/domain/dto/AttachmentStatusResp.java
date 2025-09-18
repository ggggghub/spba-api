package com.example.spba.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 响应：每个 voucherNo 的状态
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentStatusResp {
    private String voucherNo;
    private Boolean uploaded;
    private String latestFile; // 最新一条的文件名（按 seq 最大）
}