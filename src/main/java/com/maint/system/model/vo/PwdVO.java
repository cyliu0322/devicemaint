package com.maint.system.model.vo;

/**
 * 用户修改密码
 * @author aisino
 *
 */
public class PwdVO {
	private String original;	// 旧密码
	private String password;	// 新密码
	private String confirm;		// 确认密码
	
	public String getOriginal() {
		return original;
	}
	
	public void setOriginal(String original) {
		this.original = original;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getConfirm() {
		return confirm;
	}
	
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	
}
