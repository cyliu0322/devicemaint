package com.maint.system.enums;

public enum MaintenanceOrderStatusEnum {

	CJDD("1","创建订单"),//包括创建分配
	BYZ("2","保养中"),
	BYWC("3","保养完成"),
	YHF("4","已回访");
	
	private String value;
	private String txt;
	
	private MaintenanceOrderStatusEnum(String value, String txt) {
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
	
	public static MaintenanceOrderStatusEnum getvalueOf(String value){
		if(value!=null){
			for(MaintenanceOrderStatusEnum maintenanceOrderStatusEnum:MaintenanceOrderStatusEnum.values()){
				if(value.equals(maintenanceOrderStatusEnum.getValue())){
					return maintenanceOrderStatusEnum;
				}
			}
		}
		return null;
	}
	
	public static MaintenanceOrderStatusEnum txtOf(String txt){
		if(txt!=null){
			for(MaintenanceOrderStatusEnum maintenanceOrderStatusEnum:MaintenanceOrderStatusEnum.values()){
				if(txt.equalsIgnoreCase(maintenanceOrderStatusEnum.getTxt())){
					return maintenanceOrderStatusEnum;
				}
			}
		}
		return null;
	}
	public static MaintenanceOrderStatusEnum nameOf(String name){
		for(MaintenanceOrderStatusEnum maintenanceOrderStatusEnum: MaintenanceOrderStatusEnum.values()){
			if(maintenanceOrderStatusEnum.name().equals(name)){
				return maintenanceOrderStatusEnum;
			}
		}
		return null;
	}
}
