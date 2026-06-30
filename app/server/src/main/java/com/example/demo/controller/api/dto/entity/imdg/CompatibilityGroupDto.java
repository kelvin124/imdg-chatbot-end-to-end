package com.example.demo.controller.api.dto.entity.imdg;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CompatibilityGroupDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("allowedWith")
    private List<String> allowedWith;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getAllowedWith() {
        return allowedWith;
    }

    public void setAllowedWith(List<String> allowedWith) {
        this.allowedWith = allowedWith;
    }
}