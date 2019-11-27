package com.maint.system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maint.common.util.DateUtils;
import com.maint.common.util.ShiroUtil;
import com.maint.common.util.StringUtil;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.mapper.MaintenanceOrderMapper;
import com.maint.system.mapper.MaintenanceTraceMapper;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.MaintenanceTrace;
import com.maint.system.model.OrderStatusBean;

@Service
public class MobileLoginService {
	
	@Autowired
	private MaintainTraceMapper miantainTraceMapper;
	@Autowired
	private MaintenanceTraceMapper maintenanceTraceMapper;
	@Autowired
	private MaintenanceOrderMapper maintenanceOrderMapper;
	@Autowired
	private MaintainOrderMapper maintainOrderMapper;
	
	@SuppressWarnings("unchecked")
	public <T> T getOrderInfo(String orderId, String orderType) {
		
		T order = null;
		
		if(OrderTypeEnum.WX.getValue().equals(orderType)) {
			order = (T) maintainOrderMapper.selectOrderByOrderId(orderId);
		}else {
			order = (T) maintenanceOrderMapper.selectOrderByOrderId(orderId);
		}
		
		return order;
	}
	
	/**
	 * 获取订单追踪信息
	 * @param orderId 订单号
	 * @param orderType 订单类型，0：维修订单，1：保养订单
	 * @return
	 */
	public List<OrderStatusBean> getOrderTraceInfo(String orderId, String orderType){
		List<OrderStatusBean> orderStatusBeans = new ArrayList<OrderStatusBean>();
		
		if(OrderTypeEnum.WX.getValue().equals(orderType)) {
			//维修单
			List<MaintainTrace> maintainTraces = miantainTraceMapper.selectOrderTraceByOrderId(orderId);
			
			maintainTraces.stream().forEachOrdered(maintainTrace -> {
				OrderStatusBean orderStatusBean = new OrderStatusBean();
				orderStatusBean.setOrderStatusDescription(
						MaintainOrderStatusEnum.getvalueOf(maintainTrace.getOrderStatus()).getTxt());
				orderStatusBean.setFaultCause(maintainTrace.getFaultCause());
				orderStatusBean.setDate(DateUtils.parseDateToStr("yyyy/MM/dd HH:mm:ss", maintainTrace.getMaintainDate()));
				
				orderStatusBeans.add(orderStatusBean);
			});
			
		}else if (OrderTypeEnum.BY.getValue().equals(orderType)) {
			//保养单
			List<MaintenanceTrace> maintenanceTraces = maintenanceTraceMapper.selectOrderTraceByOrderId(orderId);
			
			maintenanceTraces.stream().forEachOrdered(maintenanceTrace -> {
				OrderStatusBean orderStatusBean = new OrderStatusBean();
				orderStatusBean.setOrderStatusDescription(
						MaintainOrderStatusEnum.getvalueOf(maintenanceTrace.getOrderStatus()).getTxt());
				orderStatusBean.setFaultCause(maintenanceTrace.getFaultCause());
				orderStatusBean.setDate(DateUtils.parseDateToStr("yyyy/MM/dd HH:mm:ss", maintenanceTrace.getMaintenanceDate()));
				
				orderStatusBeans.add(orderStatusBean);
			});
		}
		
		return orderStatusBeans;
	}
	
	/**
	 * 搜索订单信息
	 * @param <E>
	 * @param order_id 订单号
	 * @param flag 查询标志，为0则代表通过订单号查询，为1则代表通过用户id查询前5条订单
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> searchOrderService(String order_id, String flag) throws Exception{
		List<E> orderAssigns = null;
		
		if("0".equals(flag)) {
			//orderAssigns = (List<E>) orderAssignMapper.selectOrderAssignsByOrderId(order_id, ShiroUtil.getCurrentUser().getUserId());
		}else {
			//orderAssigns = (List<E>) orderAssignMapper.selectOrderAssignsByUserId(ShiroUtil.getCurrentUser().getUserId());
		}
		
		return orderAssigns;
	}

}
