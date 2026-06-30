package com.example.demo.controller.api.dto.entity.vessel.structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BayDto {

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("vesselId")
    private String vesselId;

    @JsonProperty("bayIndex")
    private Integer bayIndex;

    @JsonProperty("vesselName")
    private String vesselName;

    @JsonProperty("lcg")
    private Double lcg;

    @JsonProperty("minShear")
    private Double minShear;

    @JsonProperty("maxShear")
    private Double maxShear;

    @JsonProperty("maxBending")
    private Double maxBending;

    @JsonProperty("constWeight")
    private Double constWeight;

    @JsonProperty("constWeightVcg")
    private Double constWeightVcg;

    @JsonProperty("rows")
    private List<RowDto> rowDtoList;

    @JsonProperty("buoyancyPoints")
    private List<Double> buoyancyPoints;

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Double getLcg() {
        return lcg;
    }

    public void setLcg(Double lcg) {
        this.lcg = lcg;
    }

    public Double getMinShear() {
        return minShear;
    }

    public void setMinShear(Double minShear) {
        this.minShear = minShear;
    }

    public Double getMaxShear() {
        return maxShear;
    }

    public void setMaxShear(Double maxShear) {
        this.maxShear = maxShear;
    }

    public Double getMaxBending() {
        return maxBending;
    }

    public void setMaxBending(Double maxBending) {
        this.maxBending = maxBending;
    }

    public Double getConstWeight() {
        return constWeight;
    }

    public void setConstWeight(Double constWeight) {
        this.constWeight = constWeight;
    }

    public Double getConstWeightVcg() {
        return constWeightVcg;
    }

    public void setConstWeightVcg(Double constWeightVcg) {
        this.constWeightVcg = constWeightVcg;
    }

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public List<Double> getBuoyancyPoints() {
        return buoyancyPoints;
    }

    public void setBuoyancyPoints(List<Double> buoyancyPoints) {
        this.buoyancyPoints = buoyancyPoints;
    }

    public List<RowDto> getRowDtoList() {
        return rowDtoList;
    }

    public void setRowDtoList(List<RowDto> rowDtoList) {
        this.rowDtoList = rowDtoList;
    }
}