package com.example.demo.controller.api.dto.entity.imdg;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SegregationGroupCodeDto {

    @JsonProperty("code")
    private String code;

    @JsonProperty("group")
    private String group;

    @JsonProperty("description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}