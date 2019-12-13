package com.maint.system.model;

import java.util.List;

/**
 * 前端品牌管理Form绑定对象
 * @author aisino
 *
 */
public class BrandAndStep {
	private String brandName;		//品牌名称
	private List<String> maintSteps;	//维修流程
	private List<String> keepSteps;		//保养流程
	private int maintIndex;			//前端维修流程数目，用于maintSteps索引
	private int keepIndex;			//前端保养流程数目，用于keepSteps索引
    
	public String getBrandName() {
		return brandName;
	}
	
	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}
	
	public List<String> getMaintSteps() {
		return maintSteps;
	}
	
	public void setMaintSteps(List<String> maintSteps) {
		this.maintSteps = maintSteps;
	}
	
	public List<String> getKeepSteps() {
		return keepSteps;
	}
	
	public void setKeepSteps(List<String> keepSteps) {
		this.keepSteps = keepSteps;
	}
	
	public int getMaintIndex() {
		return maintIndex;
	}
	
	public void setMaintIndex(int maintIndex) {
		this.maintIndex = maintIndex;
	}
	
	public int getKeepIndex() {
		return keepIndex;
	}
	
	public void setKeepIndex(int keepIndex) {
		this.keepIndex = keepIndex;
	}
	
}