package com.example.demo.service.param;

import com.example.demo.controller.api.request.stability.ValidateKgRequest;

public class ValidateKgParam {

    private String stowagePlanId;

    public ValidateKgParam(ValidateKgRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
    }

    public ValidateKgParam(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }
}