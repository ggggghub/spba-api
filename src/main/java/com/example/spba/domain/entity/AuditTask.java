package com.example.spba.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName("audit_task")
public class AuditTask {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 客户唯一识别 */
    private String identityNumber;

    /** 公司名称 */
    private String companyName;

    /** 任务文件路径（可选：Excel/JSON） */
    private String taskFile;

    /** 任务日期（用于目录构建） */
    private LocalDate taskDate;

    /** 创建时间 */
    private Date createdAt;
}