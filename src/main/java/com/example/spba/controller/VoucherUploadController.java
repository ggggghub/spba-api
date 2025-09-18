package com.example.spba.controller;

import com.example.spba.domain.entity.AuditVoucherFile;
import com.example.spba.service.VoucherUploadService;
import com.example.spba.utils.R;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/voucher")
public class VoucherUploadController {

    @Resource
    private VoucherUploadService voucherUploadService;

    /** 上传凭证附件 */
    @PostMapping("/{identityNumber}/{voucherNo}/upload")
    public R uploadFiles(@PathVariable String identityNumber,
                         @PathVariable String voucherNo,
                         @RequestParam("files") MultipartFile[] files) {
        List<AuditVoucherFile> list = voucherUploadService.uploadFiles(identityNumber, voucherNo, files);
        return R.success(list, "上传成功");
    }

    /** 查询某凭证下的上传记录 */
    @GetMapping("/{identityNumber}/{voucherNo}")
    public R getFiles(@PathVariable String identityNumber,
                      @PathVariable String voucherNo) {
        List<AuditVoucherFile> list = voucherUploadService.getFiles(identityNumber, voucherNo);
        return R.success(list);
    }

    /** 查询某企业的所有附件 */
    @GetMapping("/identity/{identityNumber}")
    public R getFilesByIdentity(@PathVariable String identityNumber) {
        List<AuditVoucherFile> list = voucherUploadService.getFilesByIdentityNumber(identityNumber);
        return R.success(list);
    }
}
