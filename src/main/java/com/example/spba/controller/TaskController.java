package com.example.spba.controller;

import com.example.spba.domain.entity.AuditTask;
import com.example.spba.service.TaskService;
import com.example.spba.utils.ExcelParser;
import com.example.spba.utils.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    /** 解析 Excel（默认最后一个 sheet，也可指定 sheetIndex） */
    @GetMapping("/parse")
    public R parseExcel(@RequestParam String filePath,
                        @RequestParam(required = false) Integer sheetIndex) {
        try {
            ExcelParser parser = (sheetIndex == null)
                    ? new ExcelParser(filePath)
                    : new ExcelParser(filePath, sheetIndex);

            // 注意这里用 Map<String,Object> 接收，因为 parser 返回的是 {titleName, indexNo, data}
            Map<String, Object> result = parser.parse();
            return R.success(result, "解析成功");

        } catch (Exception e) {
            return R.error("解析失败: " + e.getMessage());
        }
    }
}
