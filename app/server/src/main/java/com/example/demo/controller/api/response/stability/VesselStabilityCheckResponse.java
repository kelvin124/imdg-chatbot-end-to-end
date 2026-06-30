package com.example.demo.controller.api.response.stability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VesselStabilityCheckResponse {

    @JsonProperty("isStable")
    private Boolean isStable;

    @JsonProperty("cg")
    private Double cg;

    @JsonProperty("kg")
    private Double kg;

    @JsonProperty("isCgAcceptable")
    private Boolean isCgAcceptable;

    @JsonProperty("isKgAcceptable")
    private Boolean isKgAcceptable;

    public Double getCg() {
        return cg;
    }

    public void setCg(Double cg) {
        this.cg = cg;
    }

    public Boolean getIsCgAcceptable() {
        return isCgAcceptable;
    }

    public void setIsCgAcceptable(Boolean cgAcceptable) {
        isCgAcceptable = cgAcceptable;
    }

    public Boolean getIsKgAcceptable() {
        return isKgAcceptable;
    }

    public void setIsKgAcceptable(Boolean kgAcceptable) {
        isKgAcceptable = kgAcceptable;
    }

    public Boolean getIsStable() {
        return isStable;
    }

    public void setIsStable(Boolean stable) {
        isStable = stable;
    }

    public Double getKg() {
        return kg;
    }

    public void setKg(Double kg) {
        this.kg = kg;
    }

}
