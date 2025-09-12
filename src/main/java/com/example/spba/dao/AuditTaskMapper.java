package com.example.spba.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.spba.domain.entity.AuditTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuditTaskMapper extends BaseMapper<AuditTask> {
}