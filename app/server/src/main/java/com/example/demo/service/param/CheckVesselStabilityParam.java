package com.example.demo.service.param;

import com.example.demo.controller.api.request.stability.CheckVesselStabilityRequest;

public class CheckVesselStabilityParam {

    private String stowagePlanId;

    public CheckVesselStabilityParam(CheckVesselStabilityRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
    }

    public CheckVesselStabilityParam(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }
}