package com.example.demo.controller.api.dto.entity.stowage;

import com.example.demo.controller.api.dto.entity.vessel.profile.TankSnapshotDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VesselProfileSnapshotDto {

    @JsonProperty("snapshotDate")
    private Instant snapshotDate;

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("vesselName")
    private String vesselName;

    @JsonProperty("shipInfo")
    private ShipInfoSnapshotDto shipInfoSnapshotDto;

    @JsonProperty("hydroPoints")
    private List<HydroPointSnapshotDto> hydroPointSnapshotDtoList;

    @JsonProperty("tanks")
    private List<TankSnapshotDto> tankSnapshotDtoList;

    @JsonProperty("vesselId")
    private String vesselId;

    public List<HydroPointSnapshotDto> getHydroPointSnapshotDtoList() {
        return hydroPointSnapshotDtoList;
    }

    public void setHydroPointSnapshotDtoList(List<HydroPointSnapshotDto> hydroPointSnapshotDtoList) {
        this.hydroPointSnapshotDtoList = hydroPointSnapshotDtoList;
    }

    public ShipInfoSnapshotDto getShipInfoSnapshotDto() {
        return shipInfoSnapshotDto;
    }

    public void setShipInfoSnapshotDto(ShipInfoSnapshotDto shipInfoSnapshotDto) {
        this.shipInfoSnapshotDto = shipInfoSnapshotDto;
    }

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public List<TankSnapshotDto> getTankSnapshotDtoList() {
        return tankSnapshotDtoList;
    }

    public void setTankSnapshotDtoList(List<TankSnapshotDto> tankSnapshotDtoList) {
        this.tankSnapshotDtoList = tankSnapshotDtoList;
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
}
