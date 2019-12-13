package com.maint.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.maint.system.model.MaintainOrder;

public interface MaintainOrderMapper {
	
	/**
	 * 更改维修订单状态
	 * @param maintainOrderId 维修订单号
	 * @param state 状态
	 * @return
	 */
	@Update(value = "UPDATE tbl_maintain_order SET state = #{state} WHERE maintain_order_id = #{maintainOrderId}")
	int updateState(@Param("maintainOrderId") String maintainOrderId, @Param("state") String state);
	
	@Select(value = "SELECT * FROM tbl_maintain_order WHERE maintain_order_id = #{orderId} limit 1")
	MaintainOrder selectOrderByOrderId(@Param("orderId") String orderId);
	
	@Select(value = "SELECT * FROM tbl_maintain_order WHERE maintain_order_id = #{orderId} AND user_id = #{userId} limit 1")
	List<MaintainOrder> selectOrder(@Param("orderId") String orderId, @Param("userId") int userId);
	
	List<MaintainOrder> selectOrderByConditions(Map<String, Object> paraMap);
	
    int deleteByPrimaryKey(String maintainOrderId);

    int insert(MaintainOrder record);

    int insertSelective(MaintainOrder record);

    MaintainOrder selectByPrimaryKey(String maintainOrderId);
    
    /**
     * 获取所有维修单
     * @param maintQuery
     * @return
     */
    List<MaintainOrder> selectAllWithQuery(MaintainOrder maintQuery);

    int updateByPrimaryKeySelective(MaintainOrder record);

    int updateByPrimaryKey(MaintainOrder record);
}