package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.MaintenanceOrder;

public interface MaintenanceOrderMapper {
	@Select(value = "SELECT * FROM tbl_maintenance_order WHERE maintenance_order_id = #{orderId}")
	List<MaintenanceOrder> selectOrderByOrderId(@Param("orderId") String orderId);
	
    int deleteByPrimaryKey(String maintenanceOrderId);

    int insert(MaintenanceOrder record);

    int insertSelective(MaintenanceOrder record);

    MaintenanceOrder selectByPrimaryKey(String maintenanceOrderId);

    int updateByPrimaryKeySelective(MaintenanceOrder record);

    int updateByPrimaryKey(MaintenanceOrder record);
}