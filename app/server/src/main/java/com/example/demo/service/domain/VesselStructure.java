package com.example.demo.service.domain;

import com.example.demo.entity.vessel.profile.VesselProfile;
import com.example.demo.entity.vessel.structure.Bay;

import java.util.List;

public class VesselStructure {

    private String vesselId;
    private VesselProfile vesselProfile;
    private List<Bay> bays;

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public VesselProfile getVesselProfile() {
        return vesselProfile;
    }

    public void setVesselProfile(VesselProfile vesselProfile) {
        this.vesselProfile = vesselProfile;
    }

    public List<Bay> getBays() {
        return bays;
    }

    public void setBays(List<Bay> bays) {
        this.bays = bays;
    }

}
