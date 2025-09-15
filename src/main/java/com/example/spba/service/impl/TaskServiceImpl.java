package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.spba.domain.entity.AuditTask;
import com.example.spba.dao.AuditTaskMapper;
import com.example.spba.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private AuditTaskMapper auditTaskMapper;

    @Override
    public AuditTask createTask(String identityNumber, String companyName, String taskFilePath) {
        AuditTask task = new AuditTask();
        task.setIdentityNumber(identityNumber);
        task.setCompanyName(companyName);
        task.setTaskFile(taskFilePath);
        task.setTaskDate(LocalDate.now());
        task.setState(0);
        auditTaskMapper.insert(task);
        return task;
    }

    @Override
    public List<AuditTask> getTasksByIdentity(String identityNumber) {
        return auditTaskMapper.selectList(
            new LambdaQueryWrapper<AuditTask>()
                .eq(AuditTask::getIdentityNumber, identityNumber)
                .orderByDesc(AuditTask::getCreatedAt)
        );
    }
}