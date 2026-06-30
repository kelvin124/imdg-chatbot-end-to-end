package com.example.demo.controller.api.dto.entity.vessel.structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RowDto {

    @JsonProperty("contentHash")
    private String contentHash;

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

    @Field("deck")
    private DeckDto deckDto;

    @Field("hold")
    private HoldDto holdDto;

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer index) {
        this.rowIndex = index;
    }

    public Double getLcg() {
        return lcg;
    }

    public void setLcg(Double lcg) {
        this.lcg = lcg;
    }

    public Double getTcg() {
        return tcg;
    }

    public void setTcg(Double tcg) {
        this.tcg = tcg;
    }

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

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public DeckDto getDeckDto() {
        return deckDto;
    }

    public void setDeckDto(DeckDto deckDto) {
        this.deckDto = deckDto;
    }

    public HoldDto getHoldDto() {
        return holdDto;
    }

    public void setHoldDto(HoldDto holdDto) {
        this.holdDto = holdDto;
    }
}