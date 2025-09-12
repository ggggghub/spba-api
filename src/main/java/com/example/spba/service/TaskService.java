package com.example.spba.service;

import com.example.spba.domain.entity.AuditTask;

import java.util.List;

public interface TaskService {

    /** 创建任务 */
    AuditTask createTask(String identityNumber, String companyName, String taskFilePath);

    /** 按客户查询任务 */
    List<AuditTask> getTasksByIdentity(String identityNumber);
}