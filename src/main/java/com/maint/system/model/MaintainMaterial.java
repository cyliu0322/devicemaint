package com.maint.system.model;

public class MaintainMaterial {
    private String maintainMaterialId;

    private String maintainOrderId;

    private String materialId;

    private String maintainTraceId;

    private Double amount;

    private String maintStepTraceId;

    public String getMaintainMaterialId() {
        return maintainMaterialId;
    }

    public void setMaintainMaterialId(String maintainMaterialId) {
        this.maintainMaterialId = maintainMaterialId == null ? null : maintainMaterialId.trim();
    }

    public String getMaintainOrderId() {
        return maintainOrderId;
    }

    public void setMaintainOrderId(String maintainOrderId) {
        this.maintainOrderId = maintainOrderId == null ? null : maintainOrderId.trim();
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId == null ? null : materialId.trim();
    }

    public String getMaintainTraceId() {
        return maintainTraceId;
    }

    public void setMaintainTraceId(String maintainTraceId) {
        this.maintainTraceId = maintainTraceId == null ? null : maintainTraceId.trim();
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMaintStepTraceId() {
        return maintStepTraceId;
    }

    public void setMaintStepTraceId(String maintStepTraceId) {
        this.maintStepTraceId = maintStepTraceId == null ? null : maintStepTraceId.trim();
    }
}