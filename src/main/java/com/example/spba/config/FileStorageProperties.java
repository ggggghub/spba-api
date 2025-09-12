package com.example.spba.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "storage")
public class FileStorageProperties {

    /** 客户资料根目录：如 D:/workSpace/audit/customers */
    private String root;

    /** （可选）模板目录：如 D:/workSpace/中小会计师事务所审计底稿(企业会计准则) */
    private String templateDir;
}