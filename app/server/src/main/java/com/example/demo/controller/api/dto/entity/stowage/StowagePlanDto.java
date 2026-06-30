package com.example.demo.controller.api.dto.entity.stowage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StowagePlanDto {

    @JsonProperty("planId")
    private String planId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("vesselProfileSnapshot")
    private VesselProfileSnapshotDto vesselProfileSnapshotDto;

    @JsonProperty("baySnapshots")
    private List<BaySnapshotDto> baySnapshotDtoList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public List<BaySnapshotDto> getBaySnapshotDtoList() {
        return baySnapshotDtoList;
    }

    public void setBaySnapshotDtoList(List<BaySnapshotDto> baySnapshotDtoList) {
        this.baySnapshotDtoList = baySnapshotDtoList;
    }

    public VesselProfileSnapshotDto getVesselProfileSnapshotDto() {
        return vesselProfileSnapshotDto;
    }

    public void setVesselProfileSnapshotDto(VesselProfileSnapshotDto vesselProfileSnapshotDto) {
        this.vesselProfileSnapshotDto = vesselProfileSnapshotDto;
    }
}
