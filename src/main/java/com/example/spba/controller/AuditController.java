package com.example.spba.controller;

import com.example.spba.domain.entity.AuditRecord;
import com.example.spba.service.AuditService;
import com.example.spba.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /** 查询某 identity_number 的行记录（管理员 or 客户端都走它） */
    @GetMapping("/list")
    public R list(@RequestParam String identityNumber,
                  @RequestParam(defaultValue = "false") boolean isAdmin) {
        return auditService.listByIdentity(identityNumber, isAdmin);
    }

    /** 管理员上传基础表（Excel），若已存在则报错 */
    @PostMapping("/sheet/upload")
    public R uploadAdminSheet(@RequestParam String identityNumber,
                              @RequestParam("file") MultipartFile excel) {
        return auditService.uploadAdminBaseSheet(identityNumber, excel);
    }

    /** 查询该 identity_number 是否已有管理员基础表（用于路由后判断展示哪个界面） */
    @GetMapping("/sheet/state")
    public R sheetState(@RequestParam String identityNumber) {
        return auditService.sheetState(identityNumber);
    }

    /** 行内上传附件（PDF/图片），成功后生成索引号 */
    @PostMapping("/row/upload")
    public R uploadRow(@RequestParam Long recordId,
                       @RequestParam("file") MultipartFile file) {
        return auditService.uploadRowAttachment(recordId, file);
    }

    /** 管理员更新结论 */
    @PostMapping("/row/conclusion")
    public R conclusion(@RequestParam Long recordId,
                        @RequestParam String conclusion) {
        return auditService.updateConclusion(recordId, conclusion);
    }

    /** （可选）批量保存/更新行 */
    @PostMapping("/rows/save")
    public R saveRows(@RequestParam String identityNumber,
                      @RequestBody List<AuditRecord> rows) {
        return auditService.saveOrUpdateRows(identityNumber, rows);
    }
}