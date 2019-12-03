package com.maint.system.model;

public class MaintenanceMaterial {
    private String maintenanceMaterialId;

    private String maintenanceOrderId;

    private String materialId;

    private String maintenanceTraceId;

    private Double amount;

    private String maintStepTraceId;

    public String getMaintenanceMaterialId() {
        return maintenanceMaterialId;
    }

    public void setMaintenanceMaterialId(String maintenanceMaterialId) {
        this.maintenanceMaterialId = maintenanceMaterialId == null ? null : maintenanceMaterialId.trim();
    }

    public String getMaintenanceOrderId() {
        return maintenanceOrderId;
    }

    public void setMaintenanceOrderId(String maintenanceOrderId) {
        this.maintenanceOrderId = maintenanceOrderId == null ? null : maintenanceOrderId.trim();
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId == null ? null : materialId.trim();
    }

    public String getMaintenanceTraceId() {
        return maintenanceTraceId;
    }

    public void setMaintenanceTraceId(String maintenanceTraceId) {
        this.maintenanceTraceId = maintenanceTraceId == null ? null : maintenanceTraceId.trim();
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