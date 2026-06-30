package com.example.demo.entity.vessel.profile;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class Tank {

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
    private List<BayCoverage> bayCoverages;

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

    public List<BayCoverage> getBayCoverages() {
        return bayCoverages;
    }

    public void setBayCoverages(List<BayCoverage> bayCoverages) {
        this.bayCoverages = bayCoverages;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

}
