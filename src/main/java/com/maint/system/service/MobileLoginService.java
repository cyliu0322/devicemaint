package com.maint.system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maint.common.util.DateUtils;
import com.maint.common.util.ShiroUtil;
import com.maint.system.enums.MaintainOrderStatusEnum;
import com.maint.system.enums.MaintenanceOrderStatusEnum;
import com.maint.system.enums.OrderTypeEnum;
import com.maint.system.mapper.MaintainOrderMapper;
import com.maint.system.mapper.MaintainTraceMapper;
import com.maint.system.mapper.MaintenanceOrderMapper;
import com.maint.system.mapper.MaintenanceTraceMapper;
import com.maint.system.model.MaintainOrder;
import com.maint.system.model.MaintainTrace;
import com.maint.system.model.MaintenanceOrder;
import com.maint.system.model.MaintenanceTrace;
import com.maint.system.model.OrderAidBean;
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
	 * @param orderType 查询标志，为0则代表查询维修订单表，为1则代表查询保养订单表
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <E> List<E> searchOrderService(String orderId, String orderType) throws Exception{
		List<E> orders = new ArrayList<E>();
		
		if(OrderTypeEnum.WX.getValue().equals(orderType)) {
			//维修单
			List<MaintainOrder> maintainOrders = maintainOrderMapper.selectOrder(orderId, ShiroUtil.getCurrentUser().getUserId());
			
			maintainOrders.stream().forEach(maintainOrder -> {
				OrderAidBean orderAidBean = new OrderAidBean();
				orderAidBean.setOrderId(orderId);
				orderAidBean.setOrderStatus(MaintainOrderStatusEnum.getvalueOf(maintainOrder.getState()).getTxt());
				orderAidBean.setOrderType(OrderTypeEnum.WX.getTxt());
				orderAidBean.setDeviceName(maintainOrder.getDeviceName());
				orderAidBean.setDeviceAddr(maintainOrder.getDeviceAddr());
				orders.add((E) orderAidBean);
			});
		}else {
			//保养单
			List<MaintenanceOrder> maintenanceOrders = maintenanceOrderMapper.selectOrder(orderId, ShiroUtil.getCurrentUser().getUserId());
			
			maintenanceOrders.stream().forEach(maintenanceOrder -> {
				OrderAidBean orderAidBean = new OrderAidBean();
				orderAidBean.setOrderId(orderId);
				orderAidBean.setOrderStatus(MaintenanceOrderStatusEnum.getvalueOf(maintenanceOrder.getState()).getTxt());
				orderAidBean.setOrderType(OrderTypeEnum.BY.getTxt());
				orderAidBean.setDeviceName(maintenanceOrder.getDeviceName());
				orderAidBean.setDeviceAddr(maintenanceOrder.getDeviceAddr());
				orders.add((E) orderAidBean);
			});
		}
		
		return orders;
	}
	
	/**
	 * 通过订单号获取订单类型，订单前两位为订单类型标志（“BY”：代表保养单，“WX”：代表维修单）
	 * @param orderId 订单号
	 * @return
	 * @throws Exception 
	 */
	public OrderTypeEnum getOrderTypeByOrderId(String orderId) throws Exception {
		try {
			String orderTypeFlag = orderId.substring(0, 2);
			if("BY".equals(orderTypeFlag)) {
				return OrderTypeEnum.BY;
			}else {
				return OrderTypeEnum.WX;
			}
		} catch (Exception e) {
			throw new Exception("解析订单号时出错，"+e.getMessage());
		}
	}

}
