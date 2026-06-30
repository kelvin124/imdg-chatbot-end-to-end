package com.example.demo.controller.api.dto.entity.vessel.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TankSnapshotDto {

    @JsonProperty("capTon")
    private Integer capTon;

    @JsonProperty("tcg")
    private Integer tcg;

    @JsonProperty("vcgEmpty")
    private Integer vcgEmpty;

    @JsonProperty("vcgFull")
    private Integer vcgFull;

    @JsonProperty("bayCoverages")
    private List<BayCoverageDto> bayCoverageDtoList;

    public List<BayCoverageDto> getBayCoverageDtoList() {
        return bayCoverageDtoList;
    }

    public void setBayCoverageDtoList(List<BayCoverageDto> bayCoverageDtoList) {
        this.bayCoverageDtoList = bayCoverageDtoList;
    }

    private Integer getCapTon() {
        return capTon;
    }

    public void setCapTon(Integer capTon) {
        this.capTon = capTon;
    }

    public Integer getTcg() {
        return tcg;
    }

    public void setTcg(Integer tcg) {
        this.tcg = tcg;
    }

    public Integer getVcgEmpty() {
        return vcgEmpty;
    }

    public void setVcgEmpty(Integer vcgEmpty) {
        this.vcgEmpty = vcgEmpty;
    }

    public Integer getVcgFull() {
        return vcgFull;
    }

    public void setVcgFull(Integer vcgFull) {
        this.vcgFull = vcgFull;
    }

}
