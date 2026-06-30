package com.example.demo.service.param;

public class GetStowagePlanSlotParam {

    private String stowagePlanId;
    private Integer bayIndex;
    private Integer rowIndex;
    private Integer tierIndex;
    private String operationType;

    public GetStowagePlanSlotParam() {
    }

    public GetStowagePlanSlotParam(String stowagePlanId, String operationType, Integer bayIndex,
                                   Integer rowIndex, Integer tierIndex) {
        this.bayIndex = bayIndex;
        this.operationType = operationType;
        this.rowIndex = rowIndex;
        this.stowagePlanId = stowagePlanId;
        this.tierIndex = tierIndex;
    }

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
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
