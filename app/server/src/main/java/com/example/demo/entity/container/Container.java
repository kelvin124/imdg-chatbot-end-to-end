package com.example.demo.entity.container;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = MongoDBCollection.CONTAINER)
@CompoundIndex(name = "unique_idx", def = "{ 'containerNo': 1 }", unique = true)
public class Container extends MongoDocument {

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

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getPortOfLoading() {
        return portOfLoading;
    }

    public void setPortOfLoading(String portOfLoading) {
        this.portOfLoading = portOfLoading;
    }

    public String getPortOfDischarge() {
        return portOfDischarge;
    }

    public void setPortOfDischarge(String portOfDischarge) {
        this.portOfDischarge = portOfDischarge;
    }

    public String getImdgClass() {
        return imdgClass;
    }

    public void setImdgClass(String imdgClass) {
        this.imdgClass = imdgClass;
    }

    public String getUndgCode() {
        return undgCode;
    }

    public void setUndgCode(String undgCode) {
        this.undgCode = undgCode;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Boolean getDg() {
        return isDg;
    }

    public Boolean getHighCube() {
        return isHighCube;
    }

    public Boolean getOutOofGauge() {
        return isOutOfGauge;
    }

    public Boolean getIsReefer() {
        return isReefer;
    }

    public void setIsReefer(Boolean isReefer) {
        this.isReefer = isReefer;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
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

    public void setIsOutOfGauge(Boolean outOofGauge) {
        isOutOfGauge = outOofGauge;
    }
}

