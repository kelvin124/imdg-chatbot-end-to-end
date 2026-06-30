package com.example.demo.entity.stowage;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

public class BaySnapshot {

    @Field("snapshotDate")
    private Instant snapshotDate;

    @Field("contentHash")
    private String contentHash;

    @Field("vesselName")
    private String vesselName;

    @Field("bayIndex")
    private Integer bayIndex;

    @Field("lcg")
    private Double lcg;

    @Field("minShear")
    private Double minShear;

    @Field("maxShear")
    private Double maxShear;

    @Field("maxBending")
    private Double maxBending;

    @Field("constWeight")
    private Double constWeight;

    @Field("constWeightVcg")
    private Double constWeightVcg;

    @Field("buoyancyPoints")
    private List<Double> buoyancyPoints;

    @Field("rows")
    private List<RowSnapshot> rowSnapshots;

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
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

    public List<Double> getBuoyancyPoints() {
        return buoyancyPoints;
    }

    public void setBuoyancyPoints(List<Double> buoyancyPoints) {
        this.buoyancyPoints = buoyancyPoints;
    }

    public List<RowSnapshot> getRowSnapshots() {
        return rowSnapshots;
    }

    public void setRowSnapshots(List<RowSnapshot> rowSnapshots) {
        this.rowSnapshots = rowSnapshots;
    }

    public Double getVerticalMoment() {
        return this.constWeight * this.constWeightVcg;
    }

}
