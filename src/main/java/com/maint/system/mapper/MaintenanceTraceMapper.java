package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.DateAndNum;
import com.maint.system.model.MaintenanceTrace;

public interface MaintenanceTraceMapper {
	@Select(value = "SELECT * FROM tbl_maintenance_trace WHERE maintenance_order_id = #{orderId} ORDER BY maintenance_date DESC")
	List<MaintenanceTrace> selectOrderTraceByOrderId(@Param("orderId") String orderId);
	
    int deleteByPrimaryKey(String maintenanceTraceId);
    
    int deleteByMaintenanceId(@Param("maintenanceId") String maintenanceId);

    int insert(MaintenanceTrace record);

    int insertSelective(MaintenanceTrace record);

    MaintenanceTrace selectByPrimaryKey(String maintenanceTraceId);
    
    List<MaintenanceTrace> selectByMaintenanceId(@Param("maintenanceId") String maintenanceId);
    
    /**
     * 根据user查询order
     * @param userId
     * @return
     */
    List<String> selectOrderIdsByUserId(int userId);
    
    List<DateAndNum> selectCountForApply();
    
    List<DateAndNum> selectCountForComplete();

    int updateByPrimaryKeySelective(MaintenanceTrace record);

    int updateByPrimaryKey(MaintenanceTrace record);
}