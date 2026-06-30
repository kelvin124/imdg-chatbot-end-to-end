package com.example.demo.controller.api.dto.entity.stowage.slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StowagePlanSlotPositionDto {

    @JsonProperty("bayIndex")
    private Integer bayIndex;

    @JsonProperty("rowIndex")
    private Integer rowIndex;

    @JsonProperty("tierIndex")
    private Integer tierIndex;

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
}
