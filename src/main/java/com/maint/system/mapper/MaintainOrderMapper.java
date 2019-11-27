package com.maint.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.maint.system.model.MaintainOrder;

public interface MaintainOrderMapper {
	
	@Select(value = "SELECT * FROM tbl_maintain_order WHERE maintain_order_id = #{orderId} limit 1")
	MaintainOrder selectOrderByOrderId(@Param("orderId") String orderId);
	
    int deleteByPrimaryKey(String maintainOrderId);

    int insert(MaintainOrder record);

    int insertSelective(MaintainOrder record);

    MaintainOrder selectByPrimaryKey(String maintainOrderId);
    
    List<MaintainOrder> selectAllMaint();

    int updateByPrimaryKeySelective(MaintainOrder record);

    int updateByPrimaryKey(MaintainOrder record);
}