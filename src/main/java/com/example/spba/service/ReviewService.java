package com.example.spba.service;

public interface ReviewService {

    /** 审核单条 */
    boolean reviewOne(Integer uploadId, Integer status, String comment);

    /** 批量审核 */
    int reviewBatch(Integer[] ids, Integer status, String comment);
}