package com.maint.system.model.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 新增维修单
 * @author aisino
 *
 */
public class MaintVO {
	private String companyId;
	private String companyName;
	private String companyContact;
	private String companyPhone;
	private String companyAddress;
	private int deptId;
	private String deviceId;
	private String deviceName;
	private String deviceCode;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date firstTime;
	private String brandId;
	private String maintDesc;
	private String maintContact;
	private String maintPhone;
	private String maintAddress;
	
	public String getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getCompanyContact() {
		return companyContact;
	}
	
	public void setCompanyContact(String companyContact) {
		this.companyContact = companyContact;
	}
	
	public String getCompanyPhone() {
		return companyPhone;
	}
	
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	
	public String getCompanyAddress() {
		return companyAddress;
	}
	
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	
	public int getDeptId() {
		return deptId;
	}
	
	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getDeviceCode() {
		return deviceCode;
	}
	
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}
	
	public Date getFirstTime() {
		return firstTime;
	}
	
	public void setFirstTime(Date firstTime) {
		this.firstTime = firstTime;
	}
	
	public String getBrandId() {
		return brandId;
	}
	
	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}
	
	public String getMaintDesc() {
		return maintDesc;
	}
	
	public void setMaintDesc(String maintDesc) {
		this.maintDesc = maintDesc;
	}
	
	public String getMaintContact() {
		return maintContact;
	}
	
	public void setMaintContact(String maintContact) {
		this.maintContact = maintContact;
	}
	
	public String getMaintPhone() {
		return maintPhone;
	}
	
	public void setMaintPhone(String maintPhone) {
		this.maintPhone = maintPhone;
	}
	
	public String getMaintAddress() {
		return maintAddress;
	}
	
	public void setMaintAddress(String maintAddress) {
		this.maintAddress = maintAddress;
	}

}
