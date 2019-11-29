package com.maint.system.model;

/**
 * 订单辅助类
 * @author pp
 *
 */
public class OrderAidBean {
	
	private String orderType;
	private String orderStatus;
	private String deviceName;
	private String deviceAddr;
	private String orderId;
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceAddr() {
		return deviceAddr;
	}
	public void setDeviceAddr(String deviceAddr) {
		this.deviceAddr = deviceAddr;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	@Override
	public String toString() {
		return "OrderAidBean [orderType=" + orderType + ", orderStatus=" + orderStatus + ", deviceName=" + deviceName
				+ ", deviceAddr=" + deviceAddr + ", orderId=" + orderId + "]";
	}
	
}
