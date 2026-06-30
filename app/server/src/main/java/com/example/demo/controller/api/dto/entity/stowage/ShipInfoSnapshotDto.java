package com.example.demo.controller.api.dto.entity.stowage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShipInfoSnapshotDto {

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("bayCount")
    private Integer bayCount;

    @JsonProperty("rowCount")
    private Integer rowCount;

    @JsonProperty("tierCount")
    private Integer tierCount;

    @JsonProperty("coverageRatio")
    private Double coverageRatio;

    @JsonProperty("tcgTolerance")
    private Double tcgTolerance;

    public Integer getBayCount() {
        return bayCount;
    }

    public void setBayCount(Integer bayCount) {
        this.bayCount = bayCount;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Double getTcgTolerance() {
        return tcgTolerance;
    }

    public void setTcgTolerance(Double tcgTolerance) {
        this.tcgTolerance = tcgTolerance;
    }

    public Integer getTierCount() {
        return tierCount;
    }

    public void setTierCount(Integer tierCount) {
        this.tierCount = tierCount;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Double getCoverageRatio() {
        return coverageRatio;
    }

    public void setCoverageRatio(Double coverageRatio) {
        this.coverageRatio = coverageRatio;
    }
}
