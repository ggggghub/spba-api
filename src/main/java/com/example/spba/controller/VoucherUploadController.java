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

    /** 上传发票（用户调用，可以多文件） */
    @PostMapping("/{taskId}/{voucherNo}/upload")
    public R uploadFiles(@PathVariable Integer taskId,
                         @PathVariable String voucherNo,
                         @RequestParam String uploader,
                         @RequestParam("files") MultipartFile[] files) {
        List<AuditVoucherFile> list = voucherUploadService.uploadFiles(taskId, voucherNo, uploader, files);
        return R.success(list, "上传成功");
    }

    /** 查询某凭证下的上传记录 */
    @GetMapping("/{taskId}/{voucherNo}")
    public R getFiles(@PathVariable Integer taskId,
                      @PathVariable String voucherNo) {
        List<AuditVoucherFile> list = voucherUploadService.getFiles(taskId, voucherNo);
        return R.success(list);
    }
}