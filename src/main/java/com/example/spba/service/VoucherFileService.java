package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.dto.AttachmentStatusResp;
import com.example.spba.domain.dto.UploadResultDTO;
import com.example.spba.domain.entity.VoucherFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VoucherFileService extends IService<VoucherFile> {
    UploadResultDTO upload(MultipartFile file, String identityNumber, String indexNo, String voucherNo);
    List<VoucherFile> listByIndex(String identityNumber, String indexNo);
    boolean removeOne(Long id);

    List<AttachmentStatusResp> status(String identityNumber, String indexNo, List<String> voucherNos);

    // 管理相关
    boolean verifyLatest(String identityNumber, String indexNo, String voucherNo, Integer validStatus, String remark, String operator);
    boolean adminDelete(Long id, String operator);
}
