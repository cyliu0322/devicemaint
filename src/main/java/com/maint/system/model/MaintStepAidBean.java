package com.maint.system.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class MaintStepAidBean implements Serializable{
	
	private String stepName;
	private String stepId;
	private String isComplete;
	
	@JsonProperty("LAY_CHECKED")
	private boolean isMaint;
	
	public void setMaint(boolean isMaint) {
		this.isMaint = isMaint;
	}
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public String getStepId() {
		return stepId;
	}
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	public String getIsComplete() {
		return isComplete;
	}
	public void setIsComplete(String isComplete) {
		this.isComplete = isComplete;
	}
	public boolean getIsMaint() {
		return isMaint;
	}
	public void setIsMaint(boolean isMaint) {
		this.isMaint = isMaint;
	}
	
}
