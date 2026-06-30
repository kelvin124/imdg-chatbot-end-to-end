package com.example.demo.controller.api.dto.entity.vessel.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VesselProfileDto {

    @JsonProperty("vesselId")
    private String vesselId;

    @JsonProperty("vesselName")
    private String vesselName;

    @JsonProperty("shipInfo")
    private ShipInfoDto shipInfoDto;

    @JsonProperty("hydroPoints")
    private List<HydroPointDto> hydroPointDtoList;

    @JsonProperty("tanks")
    private List<TankSnapshotDto> tankSnapshotDtoList;

    public List<HydroPointDto> getHydroPointDtoList() {
        return hydroPointDtoList;
    }

    public void setHydroPointDtoList(List<HydroPointDto> hydroPointDtoList) {
        this.hydroPointDtoList = hydroPointDtoList;
    }

    public ShipInfoDto getShipInfoDto() {
        return shipInfoDto;
    }

    public void setShipInfoDto(ShipInfoDto shipInfoDto) {
        this.shipInfoDto = shipInfoDto;
    }

    public List<TankSnapshotDto> getTankDtoList() {
        return tankSnapshotDtoList;
    }

    public void setTankDtoList(List<TankSnapshotDto> tankSnapshotDtoList) {
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
