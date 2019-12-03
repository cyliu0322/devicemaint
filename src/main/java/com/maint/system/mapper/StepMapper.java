package com.maint.system.mapper;

import com.maint.system.model.Step;

public interface StepMapper {
    int deleteByPrimaryKey(String stepId);

    int insert(Step record);

    int insertSelective(Step record);

    Step selectByPrimaryKey(String stepId);

    int updateByPrimaryKeySelective(Step record);

    int updateByPrimaryKey(Step record);
}