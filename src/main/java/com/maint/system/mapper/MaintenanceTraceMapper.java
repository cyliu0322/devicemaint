package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.MaintenanceTrace;

public interface MaintenanceTraceMapper {
	@Select(value = "SELECT * FROM tbl_maintenance_trace WHERE maintenance_order_id = #{orderId} ORDER BY maintenance_date DESC")
	List<MaintenanceTrace> selectOrderTraceByOrderId(@Param("orderId") String orderId);
	
    int deleteByPrimaryKey(String maintenanceTraceId);

    int insert(MaintenanceTrace record);

    int insertSelective(MaintenanceTrace record);

    MaintenanceTrace selectByPrimaryKey(String maintenanceTraceId);
    
    List<MaintenanceTrace> selectByUpkeepId(@Param("upkeepId") String upkeepId);

    int updateByPrimaryKeySelective(MaintenanceTrace record);

    int updateByPrimaryKey(MaintenanceTrace record);
}