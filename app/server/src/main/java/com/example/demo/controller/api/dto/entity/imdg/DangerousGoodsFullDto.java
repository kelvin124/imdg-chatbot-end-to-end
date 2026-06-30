package com.example.demo.controller.api.dto.entity.imdg;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DangerousGoodsFullDto {

    @JsonProperty("unNo")
    private String unNo;

    @JsonProperty("psn")
    private String psn;

    @JsonProperty("class")
    private String dgClass;

    @JsonProperty("classSubstance")
    private String classSubstance;

    @JsonProperty("division")
    private String division;

    @JsonProperty("divisionSubstance")
    private String divisionSubstance;

    @JsonProperty("subsidiaryHazard")
    private String subsidiaryHazard;

    @JsonProperty("segregation")
    private List<SegregationCodeDto> segregationCodeDto;

    @JsonProperty("segregationGroup")
    private List<SegregationGroupCodeDto> segregationGroupCodeDto;

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

    public List<SegregationCodeDto> getSegregationCodeDto() {
        return segregationCodeDto;
    }

    public void setSegregationCodeDto(List<SegregationCodeDto> segregationCodeDto) {
        this.segregationCodeDto = segregationCodeDto;
    }

    public List<SegregationGroupCodeDto> getSegregationGroupCodeDto() {
        return segregationGroupCodeDto;
    }

    public void setSegregationGroupCodeDto(List<SegregationGroupCodeDto> segregationGroupCodeDto) {
        this.segregationGroupCodeDto = segregationGroupCodeDto;
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

    public String getClassSubstance() {
        return classSubstance;
    }

    public void setClassSubstance(String classSubstance) {
        this.classSubstance = classSubstance;
    }

    public String getDivisionSubstance() {
        return divisionSubstance;
    }

    public void setDivisionSubstance(String divisionSubstance) {
        this.divisionSubstance = divisionSubstance;
    }
}
