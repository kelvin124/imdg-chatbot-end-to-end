package com.example.demo.controller.api.dto.entity.imdg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HazardClassDefinitionDto {

    @JsonProperty("hazardClass")
    private String hazardClass;

    @JsonProperty("substance")
    private String substance;

    public String getHazardClass() {
        return hazardClass;
    }

    public void setHazardClass(String hazardClass) {
        this.hazardClass = hazardClass;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }
}

