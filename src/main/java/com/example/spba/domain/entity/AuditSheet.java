package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("audit_sheet")
public class AuditSheet {
    @TableId
    private Long id;
    private String identityNumber;
    private String uploadedBy;   // ADMIN / USER
    private String fileUrl;      // 原始Excel路径
    private String createdAt;    // 简化：字符串承接
}