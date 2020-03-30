package com.maint.common.constants;

/**
 * 系统角色枚举
 * @author aisino
 *
 */
public enum RoleEnum {
	GLY(1),	//管理员
	FZR(2),	//维修点负责人
	SJY(3),	//首检人员
	KF(4);	//客服
	
	private int roleId;
	
	RoleEnum(int roleId) {
		this.roleId = roleId;
	}
	
	public int getRoleId() {
		return roleId;
	}
	
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}
