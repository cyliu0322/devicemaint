package com.maint.system.enums;

public enum RoleEnum {
	SUPER("1"),		// 超级管理员
	SYSTEM("2"),	// 系统管理员
	ADMIN("3"),		// 管理员
	VANGUARD("4"),	// 首检人员
	CHARGER("5"),	// 维修点负责人
	SUPPORT("6"); 	// 客服
	
	private String value;
    
    private RoleEnum(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
