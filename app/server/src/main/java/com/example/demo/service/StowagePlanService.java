package com.example.demo.service;

import com.example.demo.entity.stowage.*;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.repository.stowage.StowagePlanRepo;
import com.example.demo.repository.stowage.StowagePlanSlotRepo;
import com.example.demo.service.domain.VesselStructure;
import com.example.demo.service.exception.RecordNotFoundException;
import com.example.demo.service.exception.StowagePlanException;
import com.example.demo.service.param.CreateStowagePlanParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StowagePlanService {

    @Autowired
    private StowagePlanRepo stowagePlanRepo;

    @Autowired
    private StowagePlanSlotRepo stowagePlanSlotRepo;

    @Autowired
    private VesselStructureService vesselStructureService;

    @Autowired
    private StowagePlanSnapshotDataMapper stowagePlanSnapshotDataMapper;

    public Boolean checkIfBayHasReeferCell(String planId, Integer bayIndex) {
        var stowagePlan = this.getStowagePlanById(planId);
        var rowSnapshots = this.findBaySnapshot(stowagePlan.getBaySnapshots(), bayIndex).getRowSnapshots();
        var hasReeferCellOnDeck = false;
        var hasReeferCellInHold = false;
        for (RowSnapshot row : rowSnapshots) {
            var deck = row.getDeckSnapshot();
            var hold = row.getHoldSnapshot();
            if (deck != null) {
                hasReeferCellOnDeck = deck.getCellSnapshots().stream().anyMatch(
                        cell -> cell.getAllowReefer() != null && cell.getAllowReefer()
                );
            }
            if (hold != null) {
                hasReeferCellInHold = hold.getCellSnapshots().stream().anyMatch(
                        cell -> cell.getAllowReefer() != null && cell.getAllowReefer()
                );
            }
        }
        return hasReeferCellOnDeck || hasReeferCellInHold;
    }

    public Boolean checkIfBayExists(String planId, Integer bayIndex) {
        try {
            return this.getStowagePlanBaySnapshot(planId, bayIndex) != null;
        } catch (RecordNotFoundException e) {
            return false;
        }
    }

    public Boolean checkIfRowExists(String planId, Integer bayIndex, Integer rowIndex) {
        try {
            return this.getStowagePlanRowSnapshot(planId, bayIndex, rowIndex) != null;
        } catch (RecordNotFoundException e) {
            return false;
        }
    }

    public Boolean checkIfTierExists(String planId, Integer bayIndex, Integer rowIndex, Integer tierIndex) {
        try {
            return this.getStowagePlanCellSnapshot(planId, bayIndex, rowIndex, tierIndex) != null;
        } catch (RecordNotFoundException e) {
            return false;
        }
    }

    public StowagePlan getStowagePlanById(String stowagePlanId) {
        return stowagePlanRepo.findByPlanId(stowagePlanId).orElseThrow(
                () -> new StowagePlanException(
                        "StowagePlan id " + stowagePlanId + " does not exist",
                        Map.of("stowagePlanId", stowagePlanId)
                )
        );
    }

    @Transactional
    public void deleteStowagePlanById(String stowagePlanId) {
        stowagePlanRepo.deleteByPlanId(stowagePlanId);
        stowagePlanSlotRepo.deleteAllByPlanId(stowagePlanId);
    }

    public List<TankSnapshot> getTankSnapshot(String stowagePlanId) {
        StowagePlan stowagePlan = stowagePlanRepo.findByPlanId(stowagePlanId).orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlan does not exist.",
                        Map.ofEntries(
                                Map.entry("stowagePlanId", stowagePlanId)
                        )
                )
        );
        return stowagePlan.getVesselProfileSnapshot().getTankSnapshots();
    }

    public BaySnapshot getStowagePlanBaySnapshot(String stowagePlanId, Integer bayIndex) {
        StowagePlan stowagePlan = stowagePlanRepo.findByPlanId(stowagePlanId).orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlan does not exist.",
                        Map.ofEntries(
                                Map.entry("stowagePlanId", stowagePlanId)
                        )
                )
        );
        return stowagePlan.getBaySnapshots().stream().filter(
                bay -> bay.getBayIndex() == bayIndex
        ).findFirst().orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlan bay does not exist.",
                        Map.ofEntries(
                                Map.entry("stowagePlanId", stowagePlanId),
                                Map.entry("bayIndex", bayIndex)
                        )
                )
        );
    }

    public List<BaySnapshot> getStowagePlanBaySnapshot(String stowagePlanId) {
        StowagePlan stowagePlan = stowagePlanRepo.findByPlanId(stowagePlanId).orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlan does not exist.",
                        Map.ofEntries(
                                Map.entry("stowagePlanId", stowagePlanId)
                        )
                )
        );
        return stowagePlan.getBaySnapshots();
    }

    public RowSnapshot getStowagePlanRowSnapshot(String stowagePlanId, Integer bayIndex, Integer rowIndex) {
        BaySnapshot baySnapshot = this.getStowagePlanBaySnapshot(stowagePlanId, bayIndex);
        return baySnapshot.getRowSnapshots().stream().filter(
                row -> row.getRowIndex() == rowIndex
        ).findFirst().orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlan row does not exist.",
                        Map.ofEntries(
                                Map.entry("stowagePlanId", stowagePlanId),
                                Map.entry("bayIndex", bayIndex),
                                Map.entry("rowIndex", rowIndex)
                        )
                )
        );
    }

    public List<RowSnapshot> getStowagePlanRowSnapshot(String stowagePlanId, Integer bayIndex) {
        BaySnapshot baySnapshot = this.getStowagePlanBaySnapshot(stowagePlanId, bayIndex);
        return baySnapshot.getRowSnapshots();
    }

    public CellSnapshot getStowagePlanCellSnapshot(String stowagePlanId, Integer bayIndex, Integer rowIndex, Integer tierIndex) {
        RowSnapshot rowSnapshot = this.getStowagePlanRowSnapshot(stowagePlanId, bayIndex, rowIndex);
        DeckSnapshot deckSnapshot = rowSnapshot.getDeckSnapshot();
        HoldSnapshot holdSnapshot = rowSnapshot.getHoldSnapshot();
        CellSnapshot cellSnapshot = null;
        if (deckSnapshot != null) {
            cellSnapshot = deckSnapshot.getCellSnapshots().stream().filter(
                    cell -> cell.getTierIndex() == tierIndex
            ).findFirst().orElse(null);
        }

        if (holdSnapshot != null) {
            cellSnapshot = holdSnapshot.getCellSnapshots().stream().filter(
                    cell -> cell.getTierIndex() == tierIndex
            ).findFirst().orElse(null);
        }

        if (cellSnapshot == null) {
            throw new RecordNotFoundException(
                    "StowagePlan cell does not exist.",
                    Map.ofEntries(
                            Map.entry("stowagePlanId", stowagePlanId),
                            Map.entry("bayIndex", bayIndex),
                            Map.entry("rowIndex", rowIndex),
                            Map.entry("tierIndex", tierIndex)
                    )
            );
        }
        return cellSnapshot;
    }

    public List<CellSnapshot> getStowagePlanCellSnapshot(String stowagePlanId, Integer bayIndex, Integer rowIndex) {
        RowSnapshot rowSnapshot = this.getStowagePlanRowSnapshot(stowagePlanId, bayIndex, rowIndex);
        DeckSnapshot deckSnapshot = rowSnapshot.getDeckSnapshot();
        HoldSnapshot holdSnapshot = rowSnapshot.getHoldSnapshot();

        List<CellSnapshot> cellSnapshot = new ArrayList<>();
        if (deckSnapshot != null && deckSnapshot.getCellSnapshots() != null) {
            cellSnapshot.addAll(deckSnapshot.getCellSnapshots());
        }

        if (holdSnapshot != null && holdSnapshot.getCellSnapshots() != null) {
            cellSnapshot.addAll(holdSnapshot.getCellSnapshots());
        }

        return cellSnapshot;
    }

    public List<StowagePlanSlot> getStowagePlanSlotById(String stowagePlanId) {
        return stowagePlanSlotRepo.findAllPlannedSlots(stowagePlanId);
    }

    @Transactional
    public StowagePlan createStowagePlan(CreateStowagePlanParam createStowageParam) {
        String vesselId = createStowageParam.getVesselId();
        VesselStructure vesselStructure = vesselStructureService.getVesselStructure(vesselId);
        if (vesselStructure.getVesselProfile() == null || vesselStructure.getBays().isEmpty()) {
            throw new StowagePlanException(
                    "Vessel not found or has no bay data: " + vesselId,
                    Map.of("vesselId", vesselId)
            );
        }
        StowagePlan stowagePlan = new StowagePlan(
                stowagePlanSnapshotDataMapper.toSnapshot(vesselStructure.getVesselProfile()),
                vesselStructure.getBays().stream().map(stowagePlanSnapshotDataMapper::toSnapshot).toList()
        );
        stowagePlan.setName(createStowageParam.getName());
        stowagePlan.setPlanId(createStowageParam.getId());
        return stowagePlanRepo.save(stowagePlan);
    }

    @Transactional
    public StowagePlan syncVesselStructure(String stowagePlanId) {
        StowagePlan stowagePlan = this.getStowagePlanById(stowagePlanId);
        String vesselId = stowagePlan.getVesselProfileSnapshot().getVesselId();
        VesselStructure vesselStructure = vesselStructureService.getVesselStructure(vesselId);
        stowagePlan.setVesselProfileSnapshot(
                stowagePlanSnapshotDataMapper.toSnapshot(vesselStructure.getVesselProfile())
        );
        stowagePlan.setBaySnapshots(
                vesselStructure.getBays().stream().map(stowagePlanSnapshotDataMapper::toSnapshot).toList()
        );
        return stowagePlanRepo.save(stowagePlan);
    }

    public CellSnapshot findCellSnapshot(StowagePlan stowagePlan, Integer bayIndex, Integer rowIndex, Integer tierIndex) {
        BaySnapshot baySnapshot = this.findBaySnapshot(
                stowagePlan.getBaySnapshots(),
                bayIndex
        );
        RowSnapshot rowSnapshot = findRowSnapshot(baySnapshot, rowIndex);
        CellSnapshot cell = findCellSnapshot(rowSnapshot, tierIndex);
        if (cell == null) {
            throw new StowagePlanException(
                    "Cell does not exist, at bayIndex: " + bayIndex +
                            ", rowIndex: " + rowSnapshot.getRowIndex() +
                            ", tierIndex: " + tierIndex,
                    Map.ofEntries(
                            Map.entry("bayIndex", bayIndex),
                            Map.entry("rowIndex", rowIndex),
                            Map.entry("tierIndex", tierIndex)
                    )
            );
        }
        return cell;
    }

    private BaySnapshot findBaySnapshot(List<BaySnapshot> baySnapshots, Integer bayIndex) {
        return baySnapshots.stream().filter(
                bay -> Objects.equals(bay.getBayIndex(), bayIndex)
        ).findFirst().orElseThrow(
                () -> new StowagePlanException(
                        "Bay does not exist.",
                        Map.ofEntries(
                                Map.entry("bayIndex", bayIndex)
                        )
                )
        );
    }

    private RowSnapshot findRowSnapshot(BaySnapshot baySnapshot, Integer rowIndex) {
        return baySnapshot.getRowSnapshots().stream().filter(
                bay -> Objects.equals(bay.getRowIndex(), rowIndex)
        ).findFirst().orElseThrow(
                () -> new StowagePlanException(
                        "Row does not exist.",
                        Map.ofEntries(
                                Map.entry("bayIndex", baySnapshot.getBayIndex()),
                                Map.entry("rowIndex", rowIndex)
                        )
                )
        );
    }

    private CellSnapshot findCellSnapshot(RowSnapshot rowSnapshot, Integer tierIndex) {
        DeckSnapshot deck = rowSnapshot.getDeckSnapshot();
        HoldSnapshot hold = rowSnapshot.getHoldSnapshot();
        CellSnapshot cellSnapshot = null;
        if (deck != null) {
            cellSnapshot = deck.getCellSnapshots().stream().filter(
                    item -> item.getTierIndex().equals(tierIndex)
            ).findFirst().orElse(null);
        }
        if (cellSnapshot == null && hold != null) {
            cellSnapshot = hold.getCellSnapshots().stream().filter(
                    item -> item.getTierIndex().equals(tierIndex)
            ).findFirst().orElse(null);
        }
        return cellSnapshot;
    }

}