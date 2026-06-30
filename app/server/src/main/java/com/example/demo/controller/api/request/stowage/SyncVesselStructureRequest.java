package com.example.demo.controller.api.request.stowage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SyncVesselStructureRequest {

    @JsonProperty("stowagePlanId")
    @NotBlank(message = "Stowage Plan ID must not be empty")
    private String stowagePlanId;

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }
}
