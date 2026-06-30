package com.example.demo.entity.vessel.structure;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = MongoDBCollection.VESSEL_STRUCTURE_BAY)
@CompoundIndex(name = "unique_idx", def = "{ 'vesselId': 1, 'bayIndex': 1, 'type': 1 }", unique = true)
public class Bay extends MongoDocument {

    @Field("contentHash")
    private String contentHash;

    @Field("vesselId")
    private String vesselId;

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

    @Field("rows")
    private List<Row> rows;

    @Field("buoyancyPoints")
    private List<Double> buoyancyPoints;

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

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public List<Double> getBuoyancyPoints() {
        return buoyancyPoints;
    }

    public void setBuoyancyPoints(List<Double> buoyancyPoints) {
        this.buoyancyPoints = buoyancyPoints;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

}
