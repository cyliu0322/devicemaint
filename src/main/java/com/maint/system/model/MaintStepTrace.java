package com.maint.system.model;

import java.util.Date;

public class MaintStepTrace {
    private String id;

    private String orderId;

    private String orderTraceId;

    private String stepName;

    private String stepId;

    private String isComplete;

    private Date createDate;

    private Date completeDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getOrderTraceId() {
        return orderTraceId;
    }

    public void setOrderTraceId(String orderTraceId) {
        this.orderTraceId = orderTraceId == null ? null : orderTraceId.trim();
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName == null ? null : stepName.trim();
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId == null ? null : stepId.trim();
    }

    public String getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(String isComplete) {
        this.isComplete = isComplete == null ? null : isComplete.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }
}