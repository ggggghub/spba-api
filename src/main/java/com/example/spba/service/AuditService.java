package com.example.spba.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.spba.domain.entity.AuditRecord;
import com.example.spba.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AuditService extends IService<AuditRecord> {

    /** 用户或管理员根据 identity_number 拉取其表格（管理员传 isAdmin=true） */
    R listByIdentity(String identityNumber, boolean isAdmin);

    /** 管理员上传“基础表”（Excel），并解析为 audit_record 行 */
    R uploadAdminBaseSheet(String identityNumber, MultipartFile excel);

    /** 行内上传附件（PDF/图片），成功后生成 index_no */
    R uploadRowAttachment(Long recordId, MultipartFile file);

    /** 管理员更新审计结论（通过/不通过） */
    R updateConclusion(Long recordId, String conclusion);

    /** 查询当前 identity_number 是否已有管理员基础表 */
    R sheetState(String identityNumber);

    /** （可选）批量保存/更新记录 */
    R saveOrUpdateRows(String identityNumber, List<AuditRecord> rows);
}