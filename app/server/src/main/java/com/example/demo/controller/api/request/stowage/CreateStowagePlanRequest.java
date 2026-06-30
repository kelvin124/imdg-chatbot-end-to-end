package com.example.demo.controller.api.request.stowage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateStowagePlanRequest {

    @JsonProperty("vesselId")
    @NotBlank(message = "vesselId must not be empty")
    private String vesselId;

    @JsonProperty("name")
    @NotBlank(message = "must provide a name for the stowage plan")
    private String name;

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}