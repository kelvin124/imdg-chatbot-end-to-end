package com.example.demo.service.param;

import com.example.demo.controller.api.request.stability.ValidateCgRequest;

public class ValidateCgParam {

    private String stowagePlanId;

    public ValidateCgParam(ValidateCgRequest request) {
        this.stowagePlanId = request.getStowagePlanId();
    }

    public ValidateCgParam(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }
}