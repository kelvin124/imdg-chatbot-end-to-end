package com.example.demo.entity.stowage;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class CellSnapshot {

    @JsonProperty("snapshotDate")
    private Instant snapshotDate;

    @JsonProperty("contentHash")
    private String contentHash;

    @Field("tierIndex")
    private Integer tierIndex;

    @Field("allowReefer")
    private Boolean allowReefer;

    public Integer getTierIndex() {
        return tierIndex;
    }

    public void setTierIndex(Integer tierIndex) {
        this.tierIndex = tierIndex;
    }

    public Boolean getAllowReefer() {
        return allowReefer;
    }

    public void setAllowReefer(Boolean allowReefer) {
        this.allowReefer = allowReefer;
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
