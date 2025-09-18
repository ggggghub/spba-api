package com.example.spba.controller;

import com.example.spba.domain.dto.AttachmentStatusReq;
import com.example.spba.domain.dto.AttachmentStatusResp;
import com.example.spba.service.VoucherFileService;
import com.example.spba.utils.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherFileService voucherFileService;

    @PostMapping("/upload")
    public R upload(@RequestParam("file") MultipartFile file,
                    @RequestParam("identityNumber") String identityNumber,
                    @RequestParam("indexNo") String indexNo,
                    @RequestParam(value = "voucherNo", required = false) String voucherNo) {
        return R.success(voucherFileService.upload(file, identityNumber, indexNo, voucherNo));
    }

    @GetMapping("/attachments")
    public R list(@RequestParam("identityNumber") String identityNumber,
                  @RequestParam("indexNo") String indexNo) {
        return R.success(voucherFileService.listByIndex(identityNumber, indexNo));
    }

    @DeleteMapping("/attachment/{id}")
    public R delete(@PathVariable Long id) {
        return voucherFileService.removeOne(id) ? R.success() : R.error("删除失败");
    }

    @PostMapping("/attachments/status")
    public R attachmentsStatus(@RequestBody AttachmentStatusReq req) {
        List<AttachmentStatusResp> list =
                voucherFileService.status(req.getIdentityNumber(), req.getIndexNo(), req.getVoucherNos());
        return R.success(list);
    }
}
