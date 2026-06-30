package com.example.demo.controller.api.response.stowage;

import com.example.demo.controller.api.dto.entity.stowage.StowagePlanDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateStowagePlanResponse {

    @JsonProperty("stowagePlan")
    private StowagePlanDto stowagePlan;

    public CreateStowagePlanResponse(StowagePlanDto stowagePlan) {
        this.stowagePlan = stowagePlan;
    }

    public CreateStowagePlanResponse() {
    }

    public StowagePlanDto getStowagePlan() {
        return stowagePlan;
    }

    public void setStowagePlan(StowagePlanDto stowagePlan) {
        this.stowagePlan = stowagePlan;
    }
    
}
