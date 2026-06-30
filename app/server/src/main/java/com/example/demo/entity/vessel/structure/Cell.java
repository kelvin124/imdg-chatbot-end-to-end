package com.example.demo.entity.vessel.structure;

import org.springframework.data.mongodb.core.mapping.Field;

public class Cell {

    @Field("contentHash")
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

}
