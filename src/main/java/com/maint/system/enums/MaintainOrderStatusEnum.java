package com.maint.system.enums;

public enum MaintainOrderStatusEnum {

	WXSQ("0","维修申请"),
	DSJ("1","待首检"),
	YSJ("2","已首检"),
	DWX("3","待维修"),
	WXZ("4","维修中"),
	WXWC("5","维修完成"),
	DHF("6","待回访"),
	YHF("7","已回访");
	
	private String value;
	private String txt;
	
	private MaintainOrderStatusEnum(String value, String txt) {
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
	
	public static MaintainOrderStatusEnum getvalueOf(String value){
		if(value!=null){
			for(MaintainOrderStatusEnum maintainOrderStatusEnum:MaintainOrderStatusEnum.values()){
				if(value.equals(maintainOrderStatusEnum.getValue())){
					return maintainOrderStatusEnum;
				}
			}
		}
		return null;
	}
	
	public static MaintainOrderStatusEnum txtOf(String txt){
		if(txt!=null){
			for(MaintainOrderStatusEnum maintainOrderStatusEnum:MaintainOrderStatusEnum.values()){
				if(txt.equalsIgnoreCase(maintainOrderStatusEnum.getTxt())){
					return maintainOrderStatusEnum;
				}
			}
		}
		return null;
	}
	public static MaintainOrderStatusEnum nameOf(String name){
		for(MaintainOrderStatusEnum maintainOrderStatusEnum: MaintainOrderStatusEnum.values()){
			if(maintainOrderStatusEnum.name().equals(name)){
				return maintainOrderStatusEnum;
			}
		}
		return null;
	}
}
