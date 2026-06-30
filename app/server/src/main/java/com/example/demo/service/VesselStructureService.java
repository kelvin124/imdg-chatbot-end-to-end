package com.example.demo.service;

import com.example.demo.entity.vessel.profile.VesselProfile;
import com.example.demo.entity.vessel.structure.Bay;
import com.example.demo.entity.vessel.structure.Cell;
import com.example.demo.entity.vessel.structure.Row;
import com.example.demo.repository.bay.BayRepo;
import com.example.demo.repository.vessel.VesselProfileRepo;
import com.example.demo.service.domain.BayType;
import com.example.demo.service.domain.VesselStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VesselStructureService {

    @Autowired
    private VesselProfileRepo vesselProfileRepo;

    @Autowired
    private BayRepo bayRepo;

    public VesselStructure getVesselStructure(String vesselId) {
        VesselStructure structure = new VesselStructure();
        VesselProfile profile = vesselProfileRepo.findByVesselId(vesselId).orElse(null);
        List<Bay> bays = bayRepo.findByVesselId(vesselId);
        structure.setVesselId(vesselId);
        structure.setVesselProfile(profile);
        structure.setBays(bays);
        return structure;
    }

    public Optional<Bay> getBay(String vesselId, int bayIndex) {
        return bayRepo.findBay(vesselId, bayIndex);
    }

    public Optional<Row> getRow(String vesselId, int bayIndex, int rowIndex) {
        return bayRepo.findRow(vesselId, bayIndex, rowIndex);
    }

    public Optional<Cell> getCell(String vesselId, int bayIndex, int rowIndex, int tierIndex, String bayType) {
        Optional<Row> optRow = bayRepo.findRow(vesselId, bayIndex, rowIndex);
        if (optRow.isEmpty()) {
            return Optional.empty();
        }
        Row row = optRow.get();
        if (BayType.fromCode(bayType) == BayType.BayTypeEnum.DECK) {
            return row.getDeck().getCells().stream()
                    .filter(cell -> cell.getTierIndex() == tierIndex)
                    .findFirst();
        } else if (BayType.fromCode(bayType) == BayType.BayTypeEnum.HOLD) {
            return row.getHold().getCells().stream()
                    .filter(cell -> cell.getTierIndex() == tierIndex)
                    .findFirst();
        } else {
            throw new IllegalArgumentException("Unknown BayType");
        }
    }

    public Boolean allowReefer(String vesselId, int bayIndex, int rowIndex, int tierIndex, String bayType) {
        return getCell(vesselId, bayIndex, rowIndex, tierIndex, bayType)
                .map(Cell::getAllowReefer)
                .orElse(false);
    }
}