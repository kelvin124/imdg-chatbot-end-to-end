package com.example.demo.entity.stowage;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class BayCoverageSnapshot {

    @Field("snapshotDate")
    private Instant snapshotDate;

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

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }
}
