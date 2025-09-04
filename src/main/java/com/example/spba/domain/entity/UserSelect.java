package com.example.spba.domain.entity;

/**
 * @author ZhangX
 * 作者: JAVA祖父
 * 日期: 2025/9/2
 * 时间: 16:58
 */


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@TableName("user_select_values")
public class UserSelect {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String identityNumber;

    private String value;

    private LocalDateTime updatedAt;
}

