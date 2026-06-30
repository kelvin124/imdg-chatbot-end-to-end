package com.example.demo.entity.stowage;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class HoldSnapshot {

    @Field("contentHash")
    private String contentHash;

    @Field("identifier")
    private Integer identifier;

    @Field("maxHeight")
    private Double maxHeight;

    @Field("maxWeight20")
    private Double maxWeight20;

    @Field("maxWeight40")
    private Double maxWeight40;

    @Field("vcg")
    private Double vcg;

    @Field("cellSnapshots")
    private List<CellSnapshot> cellSnapshots;

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Double getMaxWeight20() {
        return maxWeight20;
    }

    public void setMaxWeight20(Double maxWeight20) {
        this.maxWeight20 = maxWeight20;
    }

    public Double getMaxWeight40() {
        return maxWeight40;
    }

    public void setMaxWeight40(Double maxWeight40) {
        this.maxWeight40 = maxWeight40;
    }

    public Double getVcg() {
        return vcg;
    }

    public void setVcg(Double vcg) {
        this.vcg = vcg;
    }

    public List<CellSnapshot> getCellSnapshots() {
        return cellSnapshots;
    }

    public void setCellSnapshots(List<CellSnapshot> cellSnapshots) {
        this.cellSnapshots = cellSnapshots;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }
}
