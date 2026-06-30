package com.example.demo.controller.api.dto.entity.vessel.structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeckDto {

    @JsonProperty("contentHash")
    private String contentHash;

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

    @JsonProperty("cells")
    private List<CellDto> cellDtoList;

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

    public List<CellDto> getCellDtoList() {
        return cellDtoList;
    }

    public void setCellDtoList(List<CellDto> cellDtoList) {
        this.cellDtoList = cellDtoList;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }
}
