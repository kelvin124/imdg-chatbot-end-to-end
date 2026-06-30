package com.example.demo.service.param;

import com.example.demo.controller.api.request.stability.ComputeCgRequest;

public class ComputeCgParam {

    private String stowagePlanId;

    public ComputeCgParam(ComputeCgRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
    }

    public ComputeCgParam(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }
}