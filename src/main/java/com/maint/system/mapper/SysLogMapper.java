package com.maint.system.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.maint.system.model.SysLog;

import java.util.List;

@Mapper
public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLog sysLog);

    SysLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(SysLog sysLog);

    List<SysLog> selectAll();

    int count();
}