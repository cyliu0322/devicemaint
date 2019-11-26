package com.maint.system.model;

/**
 * 订单状态记录展示辅助bean
 * @author pp
 *
 */
public class OrderStatusBean {
	
	private String orderStatusDescription;
	private String faultCause;
	private String date;
	public String getOrderStatusDescription() {
		return orderStatusDescription;
	}
	public void setOrderStatusDescription(String orderStatusDescription) {
		this.orderStatusDescription = orderStatusDescription;
	}
	public String getFaultCause() {
		return faultCause;
	}
	public void setFaultCause(String faultCause) {
		this.faultCause = faultCause;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "OrderStatusBean [orderStatusDescription=" + orderStatusDescription + ", faultCause=" + faultCause
				+ ", date=" + date + "]";
	}
	
	
}
