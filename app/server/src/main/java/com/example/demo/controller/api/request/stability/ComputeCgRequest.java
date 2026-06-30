package com.example.demo.controller.api.request.stability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComputeCgRequest {

    @JsonProperty("stowagePlanId")
    @NotBlank(message = "stowagePlanId must not be empty")
    public String stowagePlanId;

    public String getStowagePlanId() {
        return stowagePlanId;
    }
}