package com.example.demo.service.param;

import com.example.demo.controller.api.request.stowage.AddStowagePlanSlotRequest;

public class AddStowagePlanSlotParam {

    private String stowagePlanId;
    private Integer bayIndex;
    private String type;
    private Integer rowIndex;
    private Integer tierIndex;
    private String containerNo;
    private String operationType;

    public AddStowagePlanSlotParam(Integer bayIndex, Integer rowIndex, Integer tierIndex,
                                   String containerNo, String stowagePlanId,
                                   String operationType) {
        this.bayIndex = bayIndex;
        this.containerNo = containerNo;
        this.operationType = operationType;
        this.rowIndex = rowIndex;
        this.stowagePlanId = stowagePlanId;
        this.tierIndex = tierIndex;
    }

    public AddStowagePlanSlotParam(AddStowagePlanSlotRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
        this.bayIndex = request.getBayIndex();
//        this.type = request.getType();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
