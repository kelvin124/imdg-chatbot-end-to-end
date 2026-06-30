package com.example.demo.entity.stowage;

import com.example.demo.entity.vessel.profile.ShipInfoSnapshot;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

public class VesselProfileSnapshot {

    @Field("snapshotDate")
    private Instant snapshotDate;

    @Field("contentHash")
    private String contentHash;

    @Field("vesselName")
    private String vesselName;

    @Field("shipInfo")
    private ShipInfoSnapshot shipInfoSnapshot;

    @Field("hydroPoints")
    private List<HydroPointSnapshot> hydroPointSnapshots;

    @Field("tanks")
    private List<TankSnapshot> tankSnapshots;

    @Field("vesselId")
    private String vesselId;

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public List<HydroPointSnapshot> getHydroPointSnapshots() {
        return hydroPointSnapshots;
    }

    public void setHydroPointSnapshots(List<HydroPointSnapshot> hydroPointSnapshots) {
        this.hydroPointSnapshots = hydroPointSnapshots;
    }

    public ShipInfoSnapshot getShipInfoSnapshot() {
        return shipInfoSnapshot;
    }

    public void setShipInfoSnapshot(ShipInfoSnapshot shipInfoSnapshot) {
        this.shipInfoSnapshot = shipInfoSnapshot;
    }

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public List<TankSnapshot> getTankSnapshots() {
        return tankSnapshots;
    }

    public void setTankSnapshots(List<TankSnapshot> tankSnapshots) {
        this.tankSnapshots = tankSnapshots;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Double getTanksWeight(Double fullPercentage) {
        return this.tankSnapshots.stream().mapToDouble(tank -> tank.getCapTon() * fullPercentage).sum();
    }

}
