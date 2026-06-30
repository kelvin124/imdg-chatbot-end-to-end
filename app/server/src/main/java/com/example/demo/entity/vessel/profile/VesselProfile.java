package com.example.demo.entity.vessel.profile;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = MongoDBCollection.VESSEL_PROFILE)
@CompoundIndex(name = "unique_idx", def = "{ 'vesselId': 1 }", unique = true)
public class VesselProfile extends MongoDocument {

    @Field("vesselId")
    private String vesselId;

    @Field("contentHash")
    private String contentHash;

    @Field("vesselName")
    private String vesselName;

    @Field("shipInfo")
    private ShipInfoSnapshot shipInfoSnapshot;

    @Field("hydroPoints")
    private List<HydroPoint> hydroPoints;

    @Field("tanks")
    private List<Tank> tanks;

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public ShipInfoSnapshot getShipInfo() {
        return shipInfoSnapshot;
    }

    public void setShipInfo(ShipInfoSnapshot shipInfoSnapshot) {
        this.shipInfoSnapshot = shipInfoSnapshot;
    }

    public List<HydroPoint> getHydroPoints() {
        return hydroPoints;
    }

    public void setHydroPoints(List<HydroPoint> hydroPoints) {
        this.hydroPoints = hydroPoints;
    }

    public List<Tank> getTanks() {
        return tanks;
    }

    public void setTanks(List<Tank> tanks) {
        this.tanks = tanks;
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
