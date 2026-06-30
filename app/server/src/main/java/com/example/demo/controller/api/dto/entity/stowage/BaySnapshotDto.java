package com.example.demo.controller.api.dto.entity.stowage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaySnapshotDto {

    @JsonProperty("snapshotDate")
    private Instant snapshotDate;

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("bayIndex")
    private Integer bayIndex;

    @JsonProperty("vesselName")
    private String vesselName;

    @JsonProperty("lcg")
    private Double lcg;

    @JsonProperty("minShear")
    private Double minShear;

    @JsonProperty("maxShear")
    private Double maxShear;

    @JsonProperty("maxBending")
    private Double maxBending;

    @JsonProperty("constWeight")
    private Double constWeight;

    @JsonProperty("constWeightVcg")
    private Double constWeightVcg;

    @JsonProperty("buoyancyPoints")
    private List<Double> buoyancyPoints;

    @JsonProperty("rowSnapshots")
    private List<RowSnapshotDto> rowSnapshotDtoList;

    public List<Double> getBuoyancyPoints() {
        return buoyancyPoints;
    }

    public void setBuoyancyPoints(List<Double> buoyancyPoints) {
        this.buoyancyPoints = buoyancyPoints;
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

    public Double getLcg() {
        return lcg;
    }

    public void setLcg(Double lcg) {
        this.lcg = lcg;
    }

    public Double getMaxBending() {
        return maxBending;
    }

    public void setMaxBending(Double maxBending) {
        this.maxBending = maxBending;
    }

    public Double getMaxShear() {
        return maxShear;
    }

    public void setMaxShear(Double maxShear) {
        this.maxShear = maxShear;
    }

    public Double getMinShear() {
        return minShear;
    }

    public void setMinShear(Double minShear) {
        this.minShear = minShear;
    }

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
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

    public List<RowSnapshotDto> getRowSnapshotDtoList() {
        return rowSnapshotDtoList;
    }

    public void setRowSnapshotDtoList(List<RowSnapshotDto> rowSnapshotDtoList) {
        this.rowSnapshotDtoList = rowSnapshotDtoList;
    }

    @JsonProperty("hasReeferCell")
    public Boolean hasReeferCell() {
        var hasReeferCellOnDeck = false;
        var hasReeferCellInHold = false;
        for (RowSnapshotDto row : rowSnapshotDtoList) {
            var deck = row.getDeckSnapshotDtoList();
            var hold = row.getHoldSnapshotDtoList();
            if (deck != null) {
                hasReeferCellOnDeck = deck.getCellSnapshotDtoList().stream().anyMatch(
                        cell -> cell.getAllowReefer() != null && cell.getAllowReefer()
                );
            }
            if (hold != null) {
                hasReeferCellInHold = hold.getCellSnapshotDtoList().stream().anyMatch(
                        cell -> cell.getAllowReefer() != null && cell.getAllowReefer()
                );
            }
        }
        return hasReeferCellOnDeck || hasReeferCellInHold;
    }


    @JsonProperty("hasCell")
    public Boolean hasCell() {
        var hasCellOnDeck = false;
        var hasCellInHold = false;
        for (RowSnapshotDto row : rowSnapshotDtoList) {
            var deck = row.getDeckSnapshotDtoList();
            var hold = row.getHoldSnapshotDtoList();
            if (deck != null) {
                hasCellOnDeck = deck.getCellSnapshotDtoList() != null && !deck.getCellSnapshotDtoList().isEmpty();
            }
            if (hold != null) {
                hasCellInHold = hold.getCellSnapshotDtoList() != null && !hold.getCellSnapshotDtoList().isEmpty();
            }
        }
        return hasCellOnDeck || hasCellInHold;
    }

}
