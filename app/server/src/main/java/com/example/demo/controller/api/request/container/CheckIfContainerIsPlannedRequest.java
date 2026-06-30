package com.example.demo.controller.api.request.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CheckIfContainerIsPlannedRequest {

    @JsonProperty("containerNo")
    @NotBlank(message = "containerNo must not be empty")
    public String containerNo;

    @JsonProperty("operationType")
    @NotBlank(message = "operationType must not be empty")
    public String operationType;

    @JsonProperty("stowagePlanId")
    @NotBlank(message = "stowagePlanId must not be empty")
    public String stowagePlanId;

}
