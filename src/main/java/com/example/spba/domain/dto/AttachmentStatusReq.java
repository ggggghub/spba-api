package com.example.spba.domain.dto;

import lombok.Data;

import java.util.List;

// 请求：批量查询每个 voucherNo 是否已上传
@Data
public class AttachmentStatusReq {
    private String identityNumber;
    private String indexNo;
    private List<String> voucherNos;
}