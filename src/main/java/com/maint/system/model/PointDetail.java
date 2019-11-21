package com.maint.system.model;

public class PointDetail {
	private Dept dept;
	private User user;
	private String parentDept;	// 所属区域名称
	
	public Dept getDept() {
		return dept;
	}
	
	public void setDept(Dept dept) {
		this.dept = dept;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getParentDept() {
		return parentDept;
	}
	
	public void setParentDept(String parentDept) {
		this.parentDept = parentDept;
	}

}
