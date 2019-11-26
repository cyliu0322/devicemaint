package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.OrderAssign;

public interface OrderAssignMapper {
	
	/**
	 * 根据订单号获取订单分配信息
	 * @param orderId 订单号
	 * @return 订单分配列表
	 */
	@Select(value = "SELECT * FROM tbl_order_assign WHERE order_id = #{orderId} AND user_id = #{userId} ORDER BY create_date DESC limit 1")
	List<OrderAssign> selectOrderAssignsByOrderId(@Param("orderId") String orderId, @Param("userId") int userId);
	
	/**
	 * 通过用户id获取订单分派信息
	 * @param userId 用户id
	 * @return 订单分配列表
	 */
	@Select(value = "SELECT * FROM tbl_order_assign WHERE user_id = #{userId} ORDER BY create_date DESC limit 5")
	List<OrderAssign> selectOrderAssignsByUserId(@Param("userId") int userId);
	
    int insert(OrderAssign record);

    int insertSelective(OrderAssign record);
}