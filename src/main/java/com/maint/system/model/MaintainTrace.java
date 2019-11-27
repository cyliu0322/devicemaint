package com.maint.system.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maint.system.enums.MaintainOrderStatusEnum;

public class MaintainTrace {
	
    private String maintainTraceId;

    private String maintainOrderId;

    private String faultCause;

    private String video;

    private String image;

    private Integer userId;

    private Date maintainDate;

    private String orderStatus;
    
    @JsonProperty("statusDesc")
    private String statusDesc;

    public String getMaintainTraceId() {
        return maintainTraceId;
    }

    public void setMaintainTraceId(String maintainTraceId) {
        this.maintainTraceId = maintainTraceId == null ? null : maintainTraceId.trim();
    }

    public String getMaintainOrderId() {
        return maintainOrderId;
    }

    public void setMaintainOrderId(String maintainOrderId) {
        this.maintainOrderId = maintainOrderId == null ? null : maintainOrderId.trim();
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

    public Date getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(Date maintainDate) {
        this.maintainDate = maintainDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.statusDesc = MaintainOrderStatusEnum.getvalueOf(orderStatus).getTxt();
        this.orderStatus = orderStatus == null ? null : orderStatus.trim();
    }
}