package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("audit_voucher_files")
public class AuditVoucherFile {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 关联 audit_task.id */
    private Integer taskId;

    /** 凭证编号 */
    private String voucherNo;

    /** 原始文件名（展示用） */
    private String fileName;

    /** 磁盘绝对路径 */
    private String filePath;

    /** 扩展名（小写） */
    private String ext;

    /** 上传人（账号/identity_number） */
    private String uploadBy;

    /** 上传时间 */
    private Date uploadDate;

    /** 审核状态：0待审 1通过 2驳回 */
    private Integer status;

    /** 审核备注 */
    private String reviewComment;

    /** 审核时间 */
    private Date reviewedAt;
}