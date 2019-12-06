package com.maint.system.model;

import java.util.Date;

public class MaintenanceTrace {
    private String maintenanceTraceId;

    private String maintenanceOrderId;

    private String faultCause;

    private String video;

    private String image;

    private Integer userId;

    private Date maintenanceDate;

    private String orderStatus;
    
    private String statusDesc;
    
    private String nickname;

    public String getMaintenanceTraceId() {
        return maintenanceTraceId;
    }

    public void setMaintenanceTraceId(String maintenanceTraceId) {
        this.maintenanceTraceId = maintenanceTraceId == null ? null : maintenanceTraceId.trim();
    }

    public String getMaintenanceOrderId() {
        return maintenanceOrderId;
    }

    public void setMaintenanceOrderId(String maintenanceOrderId) {
        this.maintenanceOrderId = maintenanceOrderId == null ? null : maintenanceOrderId.trim();
    }

    public String getFaultCause() {
        return faultCause;
    }

    public void setFaultCause(String faultCause) {
        this.faultCause = faultCause == null ? null : faultCause.trim();
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video == null ? null : video.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(Date maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
    
}