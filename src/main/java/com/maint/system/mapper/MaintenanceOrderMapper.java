package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.maint.system.model.MaintenanceOrder;

public interface MaintenanceOrderMapper {
	@Select(value = "SELECT device_id FROM tbl_maintenance_order WHERE maintenance_order_id = #{orderId} limit 1")
	String selectDeviceIdByOrderId(@Param("orderId") String orderId);
	
	/**
	 * 更改保养订单状态
	 * @param maintenanceOrderId 保养订单号
	 * @param state 状态
	 * @return
	 */
	@Update(value = "UPDATE tbl_maintenance_order SET state = #{state} WHERE maintenance_order_id = #{maintenanceOrderId}")
	int updateState(@Param("maintenanceOrderId") String maintenanceOrderId, @Param("state") String state);
	
	@Select(value = "SELECT * FROM tbl_maintenance_order WHERE maintenance_order_id = #{orderId} limit 1")
	MaintenanceOrder selectOrderByOrderId(@Param("orderId") String orderId);
	
	@Select(value = "SELECT * FROM tbl_maintenance_order WHERE maintenance_order_id = #{orderId} AND user_id = #{userId} limit 1")
	List<MaintenanceOrder> selectOrder(@Param("orderId") String orderId, @Param("userId") int userId);
	
    int deleteByPrimaryKey(String maintenanceOrderId);

    int insert(MaintenanceOrder record);

    int insertSelective(MaintenanceOrder record);

    MaintenanceOrder selectByPrimaryKey(String maintenanceOrderId);
    
    List<MaintenanceOrder> selectAllMaintenance();

    int updateByPrimaryKeySelective(MaintenanceOrder record);

    int updateByPrimaryKey(MaintenanceOrder record);
}