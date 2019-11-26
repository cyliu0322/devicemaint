package com.maint.system.enums;

/**
 * 订单类型
 * @author pp
 *
 */
public enum OrderTypeEnum {

	WX("0","维修单"),
	BY("1","保养单");
	
	private String value;
	private String txt;
	
	private OrderTypeEnum(String value, String txt) {
		this.value = value;
		this.txt = txt;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getTxt() {
		return txt;
	}
	
	public String toString() {
		return "{[value:"+value+"],[txt:"+txt+"]}";
	}
	
	public static OrderTypeEnum getvalueOf(String value){
		if(value!=null){
			for(OrderTypeEnum orderTypeEnum:OrderTypeEnum.values()){
				if(value.equals(orderTypeEnum.getValue())){
					return orderTypeEnum;
				}
			}
		}
		return null;
	}
	
	public static OrderTypeEnum txtOf(String txt){
		if(txt!=null){
			for(OrderTypeEnum orderTypeEnum:OrderTypeEnum.values()){
				if(txt.equalsIgnoreCase(orderTypeEnum.getTxt())){
					return orderTypeEnum;
				}
			}
		}
		return null;
	}
	public static OrderTypeEnum nameOf(String name){
		for(OrderTypeEnum orderTypeEnum: OrderTypeEnum.values()){
			if(orderTypeEnum.name().equals(name)){
				return orderTypeEnum;
			}
		}
		return null;
	}
}
