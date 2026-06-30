package com.example.demo.controller.api.response.stowage;

import com.example.demo.controller.api.dto.entity.stowage.slot.StowagePlanSlotDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetStowagePlanSlotResponse {

    @JsonProperty("stowagePlanSlot")
    private StowagePlanSlotDto stowagePlanSlotDto;

    public StowagePlanSlotDto getStowagePlanSlotDto() {
        return stowagePlanSlotDto;
    }

    public void setStowagePlanSlotDto(StowagePlanSlotDto stowagePlanSlotDto) {
        this.stowagePlanSlotDto = stowagePlanSlotDto;
    }
}
