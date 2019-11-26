package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.MaintainTrace;

public interface MaintainTraceMapper {
	
	@Select(value = "SELECT * FROM tbl_maintain_trace WHERE maintain_order_id = #{orderId} ORDER BY maintain_date DESC")
	List<MaintainTrace> selectOrderTraceByOrderId(@Param("orderId") String orderId);
	
    int deleteByPrimaryKey(String maintainTraceId);

    int insert(MaintainTrace record);

    int insertSelective(MaintainTrace record);

    MaintainTrace selectByPrimaryKey(String maintainTraceId);

    int updateByPrimaryKeySelective(MaintainTrace record);

    int updateByPrimaryKey(MaintainTrace record);
}