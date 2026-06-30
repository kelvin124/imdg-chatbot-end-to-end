package com.example.demo.service.domain;

public class VesselStabilityCheckResult {

    private Boolean isStable;
    private Double cg;
    private Double kg;
    private Boolean isCgAcceptable;
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

    public Double getKg() {
        return kg;
    }

    public void setKg(Double kg) {
        this.kg = kg;
    }

    public Boolean getIsStable() {
        return isStable;
    }

    public void setIsStable(Boolean isStable) {
        this.isStable = isStable;
    }
}
