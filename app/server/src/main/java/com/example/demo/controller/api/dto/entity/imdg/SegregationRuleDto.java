package com.example.demo.controller.api.dto.entity.imdg;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SegregationRuleDto {

    @JsonProperty("division")
    private String division;

    @JsonProperty("rules")
    private Map<String, String> rules;

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }
}