package com.example.spba.controller;

import com.example.spba.domain.entity.AuditTask;
import com.example.spba.service.TaskService;
import com.example.spba.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Resource
    private TaskService taskService;

    /** 创建任务（管理员调用） */
    @PostMapping
    public R createTask(@RequestParam String identityNumber,
                        @RequestParam String companyName,
                        @RequestParam(required = false) String taskFilePath) {
        AuditTask task = taskService.createTask(identityNumber, companyName, taskFilePath);
        return R.success(task, "任务创建成功");
    }

    /** 按客户查询任务 */
    @GetMapping("/{identityNumber}")
    public R getTasks(@PathVariable String identityNumber) {
        List<AuditTask> tasks = taskService.getTasksByIdentity(identityNumber);
        return R.success(tasks);
    }
}