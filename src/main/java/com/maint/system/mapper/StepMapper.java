package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.Step;

public interface StepMapper {
	@Select(value = "SELECT * FROM tbl_step WHERE brand_id=#{brandId} ORDER BY weight ASC")
	List<Step> selectSteps(@Param("brandId") String brandId);
	
    int deleteByPrimaryKey(String stepId);
    
    int deleteByBrandId(@Param("brandId") String brandId);

    int insert(Step record);

    int insertSelective(Step record);

    Step selectByPrimaryKey(String stepId);
    
    List<Step> selectByBrandIdAndType(@Param("brandId") String brandId, @Param("type") Integer type);

    /**
     * 交换两个步骤的顺序
     */
    int swapSort(@Param("currentId") String currentId, @Param("swapId") String swapId);
    
    int updateByPrimaryKeySelective(Step record);

    int updateByPrimaryKey(Step record);
    
    int selectMaxWeight(@Param("brandId") String brandId, @Param("type") Integer type);
}