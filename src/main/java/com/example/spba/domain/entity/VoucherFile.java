package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("voucher_file")
public class VoucherFile {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String identityNumber;
    private String indexNo;
    private String voucherNo;
    private Integer seq;

    private String fileName;
    private String filePath;
    private String ext;
    private LocalDateTime uploadDate;
    private String uploadedBy;

    private Integer validStatus;     // 1=有效,0=无效,null=未判定
    private String  verifiedBy;
    private LocalDateTime verifiedAt;
    private String  remark;

    private Integer deleted;         // 0/1
    private LocalDateTime deletedAt;
    private String  deletedBy;
}
