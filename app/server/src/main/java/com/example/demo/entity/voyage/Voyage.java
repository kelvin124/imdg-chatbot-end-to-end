package com.example.demo.entity.voyage;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;

@Document(collection = MongoDBCollection.VOYAGE)
public class Voyage extends MongoDocument {

    @Field("voyageId")
    private String voyageId;

    @Field("eta")
    private Instant arrivalTime;

    @Field("etb")
    private Instant berthingTime;

    @Field("etd")
    private Instant departureTime;

    @Field("portRotation")
    private VoyagePort[] portRotation;

    public String getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(String voyageId) {
        this.voyageId = voyageId;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Instant getBerthingTime() {
        return berthingTime;
    }

    public void setBerthingTime(Instant berthingTime) {
        this.berthingTime = berthingTime;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public VoyagePort[] getPortRotation() {
        return portRotation;
    }

    public void setPortRotation(VoyagePort[] portRotation) {
        this.portRotation = portRotation;
    }

}
