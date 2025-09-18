package com.example.spba.domain.dto;// package com.example.spba.dto;
import lombok.Data;

@Data
public class UploadResultDTO {
    private String indexNo;     // HQ-5
    private Integer seq;        // 1,2,3...
    private String fileName;    // HQ-5-1.pdf
    private String filePath;    // D:\workSpace\...
    private String ext;         // pdf
}
