package com.maint.system.mapper;

import com.maint.system.model.MaintStepTrace;

public interface MaintStepTraceMapper {
    int insert(MaintStepTrace record);

    int insertSelective(MaintStepTrace record);
}