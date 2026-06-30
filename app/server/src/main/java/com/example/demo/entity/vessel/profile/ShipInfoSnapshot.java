package com.example.demo.entity.vessel.profile;

import org.springframework.data.mongodb.core.mapping.Field;

public class ShipInfoSnapshot {

    @Field("contentHash")
    private String contentHash;

    @Field("bayCount")
    private Integer bayCount;

    @Field("rowCount")
    private Integer rowCount;

    @Field("tierCount")
    private Integer tierCount;

    @Field("coverageRatio")
    private Double coverageRatio;

    @Field("tcgTolerance")
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

    public Integer getTierCount() {
        return tierCount;
    }

    public void setTierCount(Integer tierCount) {
        this.tierCount = tierCount;
    }

    public Double getTcgTolerance() {
        return tcgTolerance;
    }

    public void setTcgTolerance(Double tcgTolerance) {
        this.tcgTolerance = tcgTolerance;
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
