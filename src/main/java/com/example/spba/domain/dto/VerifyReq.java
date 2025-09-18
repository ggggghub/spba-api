package com.example.spba.domain.dto;

import lombok.Data;

@Data
public class VerifyReq {
    private String identityNumber;
    private String indexNo;
    private String voucherNo;
    private Integer validStatus; // 1有效 0无效
    private String remark;
    private String operator;     // 核验人（可从登录上下文取，这里参数演示）
}