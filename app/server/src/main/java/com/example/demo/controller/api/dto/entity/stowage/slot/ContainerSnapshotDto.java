package com.example.demo.controller.api.dto.entity.stowage.slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContainerSnapshotDto {

    @JsonProperty("snapshotDate")
    private Instant snapshotDate;

    @JsonProperty("contentHash")
    private String contentHash;

    @JsonProperty("containerNo")
    private String containerNo;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("height")
    private Double height;

    @JsonProperty("length")
    private Integer length;

    @JsonProperty("portOfLoading")
    private String portOfLoading;

    @JsonProperty("portOfDischarge")
    private String portOfDischarge;

    @JsonProperty("isDg")
    private Boolean isDg;

    @JsonProperty("imdgClass")
    private String imdgClass;

    @JsonProperty("undgCode")
    private String undgCode;

    @JsonProperty("isOog")
    private Boolean isOutOfGauge;

    @JsonProperty("isHighCube")
    private Boolean isHighCube;

    @JsonProperty("cargo")
    private String cargo;

    @JsonProperty("reefer")
    private Boolean isReefer;

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getImdgClass() {
        return imdgClass;
    }

    public void setImdgClass(String imdgClass) {
        this.imdgClass = imdgClass;
    }

    public Boolean getIsDg() {
        return isDg;
    }

    public void setIsDg(Boolean dg) {
        isDg = dg;
    }

    public Boolean getIsHighCube() {
        return isHighCube;
    }

    public void setIsHighCube(Boolean highCube) {
        isHighCube = highCube;
    }

    public Boolean getIsOutOfGauge() {
        return isOutOfGauge;
    }

    public void setIsOutOfGauge(Boolean outOfGauge) {
        isOutOfGauge = outOfGauge;
    }

    public Boolean getIsReefer() {
        return isReefer;
    }

    public void setIsReefer(Boolean isReefer) {
        this.isReefer = isReefer;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public void setPortOfLoading(String portOfLoading) {
        this.portOfLoading = portOfLoading;
    }

    public String getUndgCode() {
        return undgCode;
    }

    public void setUndgCode(String undgCode) {
        this.undgCode = undgCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

}
