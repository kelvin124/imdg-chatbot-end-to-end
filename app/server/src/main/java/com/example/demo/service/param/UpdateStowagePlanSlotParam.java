package com.example.demo.service.param;

import com.example.demo.controller.api.request.stowage.UpdateStowagePlanSlotRequest;

public class UpdateStowagePlanSlotParam {

    private String stowagePlanId;
    private Integer bayIndex;
    private Integer rowIndex;
    private Integer tierIndex;
    private String containerNo;
    private String operationType;

    public UpdateStowagePlanSlotParam(UpdateStowagePlanSlotRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
        this.bayIndex = request.getBayIndex();
        this.rowIndex = request.getRowIndex();
        this.tierIndex = request.getTierIndex();
        this.containerNo = request.getContainerNo();
        this.operationType = request.getOperationType();
    }

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public Integer getTierIndex() {
        return tierIndex;
    }

    public void setTierIndex(Integer tierIndex) {
        this.tierIndex = tierIndex;
    }

}
