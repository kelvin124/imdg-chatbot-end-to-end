package com.example.demo.service.param;

import com.example.demo.controller.api.request.stowage.DeleteStowagePlanSlotRequest;

public class DeleteStowagePlanSlotParam {

    private String stowagePlanId;
    private Integer bayIndex;
    private String type;
    private Integer rowIndex;
    private Integer tierIndex;
    private String operationType;

    public DeleteStowagePlanSlotParam(DeleteStowagePlanSlotRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
//        this.type = request.getType();
        this.bayIndex = request.getBayIndex();
        this.rowIndex = request.getRowIndex();
        this.tierIndex = request.getTierIndex();
        this.operationType = request.getOperationType();
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getTierIndex() {
        return tierIndex;
    }

    public void setTierIndex(Integer tierIndex) {
        this.tierIndex = tierIndex;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
