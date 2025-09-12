package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.spba.domain.entity.AuditVoucherFile;
import com.example.spba.dao.AuditVoucherFileMapper;
import com.example.spba.service.ReviewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private AuditVoucherFileMapper auditVoucherFileMapper;

    @Override
    public boolean reviewOne(Integer uploadId, Integer status, String comment) {
        AuditVoucherFile rec = auditVoucherFileMapper.selectById(uploadId);
        if (rec == null) {
            return false;
        }
        rec.setStatus(status);
        rec.setReviewComment(comment);
        rec.setReviewedAt(new Date());
        return auditVoucherFileMapper.updateById(rec) == 1;
    }

    @Override
    public int reviewBatch(Integer[] ids, Integer status, String comment) {
        LambdaUpdateWrapper<AuditVoucherFile> update = new LambdaUpdateWrapper<>();
        update.in(AuditVoucherFile::getId, Arrays.asList(ids))
              .set(AuditVoucherFile::getStatus, status)
              .set(AuditVoucherFile::getReviewComment, comment)
              .set(AuditVoucherFile::getReviewedAt, new Date());
        return auditVoucherFileMapper.update(null, update);
    }
}