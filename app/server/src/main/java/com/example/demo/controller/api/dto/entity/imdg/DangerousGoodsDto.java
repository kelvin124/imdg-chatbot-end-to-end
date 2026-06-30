package com.example.demo.controller.api.dto.entity.imdg;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DangerousGoodsDto {

    @JsonProperty("unNo")
    private String unNo;

    @JsonProperty("psn")
    private String psn;

    @JsonProperty("class")
    private String dgClass;

    @JsonProperty("division")
    private String division;

    @JsonProperty("compatibilityGroup")
    private String compatibilityGroup;

    @JsonProperty("subsidiaryHazard")
    private String subsidiaryHazard;

    @JsonProperty("segregation")
    private List<String> segregation;

    public String getDgClass() {
        return dgClass;
    }

    public void setDgClass(String dgClass) {
        this.dgClass = dgClass;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPsn() {
        return psn;
    }

    public void setPsn(String psn) {
        this.psn = psn;
    }

    public List<String> getSegregation() {
        return segregation;
    }

    public void setSegregation(List<String> segregation) {
        this.segregation = segregation;
    }

    public String getSubsidiaryHazard() {
        return subsidiaryHazard;
    }

    public void setSubsidiaryHazard(String subsidiaryHazard) {
        this.subsidiaryHazard = subsidiaryHazard;
    }

    public String getUnNo() {
        return unNo;
    }

    public void setUnNo(String unNo) {
        this.unNo = unNo;
    }

    public String getCompatibilityGroup() {
        return compatibilityGroup;
    }

    public void setCompatibilityGroup(String compatibilityGroup) {
        this.compatibilityGroup = compatibilityGroup;
    }
}
