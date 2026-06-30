package com.example.demo.entity.stowage;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = MongoDBCollection.STOWAGE_PLAN)
public class StowagePlan extends MongoDocument {

    @Field("planId")
    private String planId;

    @Field("name")
    private String name;

    @Field("vesselProfileSnapshot")
    private VesselProfileSnapshot vesselProfileSnapshot;

    @Field("baySnapshots")
    private List<BaySnapshot> baySnapshots;

    public StowagePlan() {
    }

    public StowagePlan(VesselProfileSnapshot vesselProfileSnapshot, List<BaySnapshot> baySnapshots) {
        this.vesselProfileSnapshot = vesselProfileSnapshot;
        this.baySnapshots = baySnapshots;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BaySnapshot> getBaySnapshots() {
        return baySnapshots;
    }

    public void setBaySnapshots(List<BaySnapshot> baySnapshots) {
        this.baySnapshots = baySnapshots;
    }

    public VesselProfileSnapshot getVesselProfileSnapshot() {
        return vesselProfileSnapshot;
    }

    public void setVesselProfileSnapshot(VesselProfileSnapshot vesselProfileSnapshot) {
        this.vesselProfileSnapshot = vesselProfileSnapshot;
    }

    public Double getLightWeight() {
        return this.baySnapshots.stream().mapToDouble(BaySnapshot::getConstWeight).sum();
    }

}
