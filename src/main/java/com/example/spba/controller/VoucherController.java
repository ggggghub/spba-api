package com.example.spba.controller;

import com.example.spba.domain.dto.AttachmentStatusReq;
import com.example.spba.domain.dto.AttachmentStatusResp;
import com.example.spba.domain.dto.VerifyReq;
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



    @PostMapping("/attachments/status")
    public R attachmentsStatus(@RequestBody AttachmentStatusReq req) {
        List<AttachmentStatusResp> list =
                voucherFileService.status(req.getIdentityNumber(), req.getIndexNo(), req.getVoucherNos());
        return R.success(list);
    }


    @PutMapping("/attachment/verify")
    public R verify(@RequestBody VerifyReq req) {
        boolean ok = voucherFileService.verifyLatest(
                req.getIdentityNumber(), req.getIndexNo(), req.getVoucherNo(),
                req.getValidStatus(), req.getRemark(), req.getOperator());
        return ok ? R.success() : R.error("未找到可核验的最新附件");
    }

    @DeleteMapping("/attachment/{id}")
    public R adminDelete(@PathVariable Long id, @RequestParam(required=false) String operator) {
        return voucherFileService.adminDelete(id, operator) ? R.success() : R.error("删除失败");
    }
}
