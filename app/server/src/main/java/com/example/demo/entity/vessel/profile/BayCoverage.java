package com.example.demo.entity.vessel.profile;

import org.springframework.data.mongodb.core.mapping.Field;

public class BayCoverage {

    @Field("contentHash")
    private String contentHash;

    @Field("bayIndex")
    private Integer bayIndex;

    @Field("coverageRatio")
    private Double coverageRatio;

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
    }

    public Double getCoverageRatio() {
        return coverageRatio;
    }

    public void setCoverageRatio(Double coverageRatio) {
        this.coverageRatio = coverageRatio;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }
}
