package com.example.demo.controller.api.response.stowage;

import com.example.demo.controller.api.dto.entity.stowage.StowagePlanDto;
import com.example.demo.controller.api.dto.entity.stowage.slot.StowagePlanSlotDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetStowagePlanResponse {

    @JsonProperty
    private StowagePlanDto stowagePlan;

    @JsonProperty
    private List<StowagePlanSlotDto> stowagePlanSlot;

    public StowagePlanDto getStowagePlan() {
        return stowagePlan;
    }

    public void setStowagePlan(StowagePlanDto stowagePlan) {
        this.stowagePlan = stowagePlan;
    }

    public List<StowagePlanSlotDto> getStowagePlanSlot() {
        return stowagePlanSlot;
    }

    public void setStowagePlanSlot(List<StowagePlanSlotDto> stowagePlanSlot) {
        this.stowagePlanSlot = stowagePlanSlot;
    }
}
