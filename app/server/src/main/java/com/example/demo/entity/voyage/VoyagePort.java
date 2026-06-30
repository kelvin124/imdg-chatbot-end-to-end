package com.example.demo.entity.voyage;

import org.springframework.data.mongodb.core.mapping.Field;

public class VoyagePort {

    @Field("portCode")
    private String portCode;

    @Field("callSequence")
    private Integer callSequence;

    @Field("eta")
    private String eta;

    @Field("etd")
    private String etd;

    public String getPortCode() {
        return portCode;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    private Integer getCallSequence() {
        return callSequence;
    }

    public void setCallSequence(Integer callSequence) {
        this.callSequence = callSequence;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getEtd() {
        return etd;
    }

    public void setEtd(String etd) {
        this.etd = etd;
    }

}
