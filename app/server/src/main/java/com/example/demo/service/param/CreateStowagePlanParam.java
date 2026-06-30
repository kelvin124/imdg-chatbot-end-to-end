package com.example.demo.service.param;

import java.util.UUID;

public class CreateStowagePlanParam {

    private String id;
    private String name;
    private String vesselId;

    public CreateStowagePlanParam(String name, String vesselId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.vesselId = vesselId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }
}
