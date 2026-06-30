package com.example.demo.controller.api.dto.entity.stowage;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowSnapshotDto {

    @JsonProperty("rowIndex")
    private Integer rowIndex;

    @JsonProperty("lcg")
    private Double lcg;

    @JsonProperty("tcg")
    private Double tcg;

    @JsonProperty("identifier")
    private Integer identifier;

    @JsonProperty("maxHeight")
    private Double maxHeight;

    @JsonProperty("maxWeight20")
    private Double maxWeight20;

    @JsonProperty("maxWeight40")
    private Double maxWeight40;

    @JsonProperty("vcg")
    private Double vcg;

    @JsonProperty("deckSnapshot")
    private DeckSnapshotDto deckSnapshotDtoList;

    @JsonProperty("holdSnapshot")
    private HoldSnapshotDto holdSnapshotDtoList;

    public DeckSnapshotDto getDeckSnapshotDtoList() {
        return deckSnapshotDtoList;
    }

    public void setDeckSnapshotDtoList(DeckSnapshotDto deckSnapshotDtoList) {
        this.deckSnapshotDtoList = deckSnapshotDtoList;
    }

    public HoldSnapshotDto getHoldSnapshotDtoList() {
        return holdSnapshotDtoList;
    }

    public void setHoldSnapshotDtoList(HoldSnapshotDto holdSnapshotDtoList) {
        this.holdSnapshotDtoList = holdSnapshotDtoList;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Double getLcg() {
        return lcg;
    }

    public void setLcg(Double lcg) {
        this.lcg = lcg;
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

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Double getTcg() {
        return tcg;
    }

    public void setTcg(Double tcg) {
        this.tcg = tcg;
    }

    public Double getVcg() {
        return vcg;
    }

    public void setVcg(Double vcg) {
        this.vcg = vcg;
    }
}
