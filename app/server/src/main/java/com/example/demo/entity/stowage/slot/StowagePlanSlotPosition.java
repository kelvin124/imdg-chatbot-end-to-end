package com.example.demo.entity.stowage.slot;

import org.springframework.data.mongodb.core.mapping.Field;

public class StowagePlanSlotPosition {

    @Field("bayIndex")
    private Integer bayIndex;

    @Field("rowIndex")
    private Integer rowIndex;

    @Field("tierIndex")
    private Integer tierIndex;

    public StowagePlanSlotPosition() {
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

    public Integer getTierNumber() {
        return tierIndex + 1;
    }

}
