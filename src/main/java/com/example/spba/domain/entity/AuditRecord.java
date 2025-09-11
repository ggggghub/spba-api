package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("audit_record")
public class AuditRecord {
    @TableId
    private Long id;
    private Long sheetId;
    private String identityNumber;

    private String date;              // yyyy-MM-dd
    private String voucherNo;
    private String businessContent;
    private String contraSubject;
    private Double debitAmount;
    private Double creditAmount;

    private String attachmentUrl;     // /files/xxx
    private String indexNo;           // IDX-xxx
    private String conclusion;        // 通过/不通过
    private String remark;
    private String createdAt;
}