package com.example.spba.controller;

import com.example.spba.service.ReviewService;
import com.example.spba.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    /** 审核单条上传记录 */
    @PostMapping("/{uploadId}")
    public R reviewOne(@PathVariable Integer uploadId,
                       @RequestParam Integer status,
                       @RequestParam(required = false) String comment) {
        boolean ok = reviewService.reviewOne(uploadId, status, comment);
        return ok ? R.success("审核完成") : R.error("审核失败");
    }

    /** 批量审核 */
    @PostMapping("/batch")
    public R reviewBatch(@RequestBody Integer[] ids,
                         @RequestParam Integer status,
                         @RequestParam(required = false) String comment) {
        int count = reviewService.reviewBatch(ids, status, comment);
        return R.success(count, "批量审核完成");
    }
}