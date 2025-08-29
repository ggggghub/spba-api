package com.example.spba.domain.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "blog", useServerConfiguration = true, createIndex = false)
public class EsBlog
{
    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text)
    private String author;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
