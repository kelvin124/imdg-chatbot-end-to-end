package com.example.demo.controller.api.dto.entity.stowage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellSnapshotDto {

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("tierIndex")
    private Integer tierIndex;

    @JsonProperty("allowReefer")
    private Boolean allowReefer;

    public Boolean getAllowReefer() {
        return allowReefer;
    }

    public void setAllowReefer(Boolean allowReefer) {
        this.allowReefer = allowReefer;
    }

    public Integer getTierIndex() {
        return tierIndex;
    }

    public void setTierIndex(Integer tierIndex) {
        this.tierIndex = tierIndex;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }
}
