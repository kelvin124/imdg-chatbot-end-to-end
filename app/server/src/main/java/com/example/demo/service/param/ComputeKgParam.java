package com.example.demo.service.param;

import com.example.demo.controller.api.request.stability.ComputeKgRequest;

public class ComputeKgParam {

    private String stowagePlanId;

    public ComputeKgParam(ComputeKgRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
    }

    public ComputeKgParam(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }
}