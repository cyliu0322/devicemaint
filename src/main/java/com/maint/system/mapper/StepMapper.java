package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.Step;

public interface StepMapper {
	@Select(value = "SELECT * FROM tbl_step WHERE brand_id=#{brandId} ORDER BY weight ASC")
	List<Step> selectSteps(@Param("brandId") String brandId);
	
    int deleteByPrimaryKey(String stepId);

    int insert(Step record);

    int insertSelective(Step record);

    Step selectByPrimaryKey(String stepId);

    int updateByPrimaryKeySelective(Step record);

    int updateByPrimaryKey(Step record);
}