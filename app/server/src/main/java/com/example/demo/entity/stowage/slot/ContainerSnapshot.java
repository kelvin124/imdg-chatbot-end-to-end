package com.example.demo.entity.stowage.slot;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class ContainerSnapshot {

    @Field("snapshotDate")
    private Instant snapshotDate;

    @Field("contentHash")
    private String contentHash;

    @Field("containerNo")
    private String containerNo;

    @Field("weight")
    private Double weight;

    @Field("height")
    private Double height;

    @Field("length")
    private Integer length;

    @Field("portOfLoading")
    private String portOfLoading;

    @Field("portOfDischarge")
    private String portOfDischarge;

    @Field("isDg")
    private Boolean isDg;

    @Field("imdgClass")
    private String imdgClass;

    @Field("undgCode")
    private String undgCode;

    @Field("isOog")
    private Boolean isOutOfGauge;

    @Field("isHighCube")
    private Boolean isHighCube;

    @Field("cargo")
    private String cargo;

    @Field("isReefer")
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

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
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

    public void setIsReefer(Boolean reefer) {
        isReefer = reefer;
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

    public Instant getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(Instant snapshotDate) {
        this.snapshotDate = snapshotDate;
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
}
