package com.maint.system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.maint.common.validate.groups.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {

	private static final long serialVersionUID = -3200103254689137288L;
	
	private Integer userId;
	
	@NotBlank(message = "用户名不能为空")
	private String username;
	
	@JsonIgnore
	@NotBlank(message = "密码不能为空", groups = Create.class)
	private String password;
	
	@JsonIgnore
	private String salt;
	
	private String tel;
	
	private String status;
	
	private Date lastLoginTime;
	
	private Date createTime;
	
	@JsonIgnore
	private Date modifyTime;
	
	@JsonIgnore
	private String activeCode;
	
	private Integer deptId;
	
	private String deptName;
	
	private List<String> roleNames;
	
	public Integer getUserId() {
		return userId;
	}
	
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public Date getModifyTime() {
		return modifyTime;
	}
	
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public String getActiveCode() {
		return activeCode;
	}
	
	public void setActiveCode(String activeCode) {
		this.activeCode = activeCode;
	}
	
	public Integer getDeptId() {
		return deptId;
	}
	
	public void setDeptId(Integer deptId) {
		this.deptId = deptId;
	}
	
	public String getDeptName() {
		return deptName;
	}
	
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	
	public List<String> getRoleNames() {
		return roleNames;
	}
	
	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}
	
	@Override
	public String toString() {
		String roleNameArr = "[";
		if (roleNames != null) {
			for (String rn : roleNames) {
				roleNameArr += rn + ", ";
			}
			roleNameArr = roleNameArr.substring(0, roleNameArr.length() - 2) + "]";
		} else {
			roleNameArr += "]";
		}
		
		return "User{" + "userId=" + userId + ", username='" + username + '\'' + ", password='" + password + '\''
				+ ", salt='" + salt + '\'' + ", tel='" + tel + '\'' + ", status='" + status + '\'' + ", lastLoginTime="
				+ lastLoginTime + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", activeCode='"
				+ activeCode + '\'' + ", deptId=" + deptId + ", deptName='" + deptName + '\'' + ", roleNames=" + roleNameArr + '}';
	}
}