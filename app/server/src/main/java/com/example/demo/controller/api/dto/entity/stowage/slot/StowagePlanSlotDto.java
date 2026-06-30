package com.example.demo.controller.api.dto.entity.stowage.slot;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StowagePlanSlotDto {

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("planId")
    private String planId;

    @JsonProperty("stowagePlanSlotPosition")
    private StowagePlanSlotPositionDto stowagePlanSlotPositionDto;

    @JsonProperty("container")
    private ContainerSnapshotDto containerSnapshotDto;

    @JsonProperty("allowReefer")
    private Boolean allowReefer;

    @JsonProperty("plannedSequence")
    private Integer plannedSequence;

    @JsonProperty("actualSequence")
    private Integer actualSequence;

    @JsonProperty("operationType")
    private String operationType;

    @JsonProperty("isRestow")
    private Boolean isRestow;

    public Integer getActualSequence() {
        return actualSequence;
    }

    public void setActualSequence(Integer actualSequence) {
        this.actualSequence = actualSequence;
    }

    public ContainerSnapshotDto getContainerSnapshotDto() {
        return containerSnapshotDto;
    }

    public void setContainerSnapshotDto(ContainerSnapshotDto containerSnapshotDto) {
        this.containerSnapshotDto = containerSnapshotDto;
    }

    public Boolean getAllowReefer() {
        return allowReefer;
    }

    public void setAllowReefer(Boolean allowReefer) {
        this.allowReefer = allowReefer;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Integer getPlannedSequence() {
        return plannedSequence;
    }

    public void setPlannedSequence(Integer plannedSequence) {
        this.plannedSequence = plannedSequence;
    }

    public StowagePlanSlotPositionDto getStowagePlanSlotPositionDto() {
        return stowagePlanSlotPositionDto;
    }

    public void setStowagePlanSlotPositionDto(StowagePlanSlotPositionDto stowagePlanSlotPositionDto) {
        this.stowagePlanSlotPositionDto = stowagePlanSlotPositionDto;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Boolean getIsRestow() {
        return isRestow;
    }

    public void setIsRestow(Boolean restow) {
        isRestow = restow;
    }
}
