package com.example.demo.controller.api.response.stowage;

import com.example.demo.controller.api.dto.entity.stowage.StowagePlanDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncVesselStructureResponse {

    @JsonProperty("stowagePlan")
    private StowagePlanDto stowagePlanDto;

    public StowagePlanDto getStowagePlanDto() {
        return stowagePlanDto;
    }

    public void setStowagePlanDto(StowagePlanDto stowagePlanDto) {
        this.stowagePlanDto = stowagePlanDto;
    }

}
