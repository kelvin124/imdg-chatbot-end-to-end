package com.example.demo.entity.stowage;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

public class TankSnapshot {

    @Field("snapshotDate")
    private Instant snapshotDate;

    @Field("contentHash")
    private String contentHash;

    @Field("capTon")
    private Integer capTon;

    @Field("lcg")
    private Integer lcg;

    @Field("tcg")
    private Integer tcg;

    @Field("vcgEmpty")
    private Integer vcgEmpty;

    @Field("vcgFull")
    private Integer vcgFull;

    @Field("bayCoverages")
    private List<BayCoverageSnapshot> bayCoverageSnapshots;

    public Integer getCapTon() {
        return capTon;
    }

    public void setCapTon(Integer capTon) {
        this.capTon = capTon;
    }

    public Integer getLcg() {
        return lcg;
    }

    public void setLcg(Integer lcg) {
        this.lcg = lcg;
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

    public List<BayCoverageSnapshot> getBayCoverageSnapshots() {
        return bayCoverageSnapshots;
    }

    public void setBayCoverageSnapshots(List<BayCoverageSnapshot> bayCoverageSnapshots) {
        this.bayCoverageSnapshots = bayCoverageSnapshots;
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
