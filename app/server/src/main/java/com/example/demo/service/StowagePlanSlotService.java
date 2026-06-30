package com.example.demo.service;

import com.example.demo.config.VesselStabilityConfig;
import com.example.demo.entity.container.Container;
import com.example.demo.entity.stowage.CellSnapshot;
import com.example.demo.entity.stowage.StowagePlan;
import com.example.demo.entity.stowage.StowagePlanSnapshotDataMapper;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.entity.stowage.slot.StowagePlanSlotPosition;
import com.example.demo.repository.stowage.StowagePlanSlotRepo;
import com.example.demo.service.exception.RecordNotFoundException;
import com.example.demo.service.exception.StowagePlanException;
import com.example.demo.service.param.AddStowagePlanSlotParam;
import com.example.demo.service.param.DeleteStowagePlanSlotParam;
import com.example.demo.service.param.GetStowagePlanSlotParam;
import com.example.demo.service.param.UpdateStowagePlanSlotParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StowagePlanSlotService {

    @Autowired
    private StowagePlanSlotRepo stowagePlanSlotRepo;

    @Autowired
    private StowagePlanService stowagePlanService;

    @Autowired
    private ContainerService containerService;

    @Autowired
    private VesselStabilityConfig vesselStabilityConfig;

    @Autowired
    private StowagePlanSnapshotDataMapper stowagePlanSnapshotDataMapper;

    public Boolean isContainerPlanned(String containerNo, String operationType, String stowagePlanId) {
        return stowagePlanSlotRepo.getSlotByContainerNoAndOpType(
                containerNo, operationType, stowagePlanId
        ).isPresent();
    }

    public List<StowagePlanSlot> getStowagePlanSlotByBay(GetStowagePlanSlotParam param) {
        return stowagePlanSlotRepo.findByBayIndex(
                param.getBayIndex(), param.getOperationType(),
                param.getStowagePlanId()
        );
    }

    public List<StowagePlanSlot> getStowagePlanSlotByBayRow(GetStowagePlanSlotParam param) {
        return stowagePlanSlotRepo.findByBayRowIndex(
                param.getBayIndex(), param.getRowIndex(), param.getOperationType(),
                param.getStowagePlanId()
        );
    }

    public StowagePlanSlot getStowagePlanSlotByBayRowTier(GetStowagePlanSlotParam param) {
        return stowagePlanSlotRepo.findByBayRowTierIndex(
                param.getBayIndex(), param.getRowIndex(), param.getTierIndex(), param.getOperationType(),
                param.getStowagePlanId()
        ).orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlanSlot not found for the given position.",
                        Map.ofEntries(
                                Map.entry("bayIndex", param.getBayIndex()),
                                Map.entry("rowIndex", param.getRowIndex()),
                                Map.entry("tierIndex", param.getTierIndex()),
                                Map.entry("operationType", param.getOperationType())
                        )
                )
        );
    }

    public Boolean checkIfStowagePlanSlotOccupied(
            String stowagePlanSlotId, Integer bayIndex, Integer rowIndex,
            Integer tierIndex, String operationType
    ) {
        return stowagePlanSlotRepo.isStowagePlanSlotOccupied(
                bayIndex, rowIndex, tierIndex,
                operationType, stowagePlanSlotId
        );
    }

    public StowagePlanSlot getStowagePlanSlot(GetStowagePlanSlotParam param) {
        return stowagePlanSlotRepo.findByBayRowTierIndex(
                param.getBayIndex(), param.getRowIndex(), param.getTierIndex(), param.getOperationType(),
                param.getStowagePlanId()
        ).orElseThrow(
                () -> new RecordNotFoundException(
                        "StowagePlanSlot not found for the given position.",
                        Map.ofEntries(
                                Map.entry("bayIndex", param.getBayIndex()),
                                Map.entry("rowIndex", param.getRowIndex()),
                                Map.entry("tierIndex", param.getTierIndex()),
                                Map.entry("operationType", param.getOperationType())
                        )
                )
        );
    }

    @Transactional
    public StowagePlanSlot addStowagePlanSlot(AddStowagePlanSlotParam param) {

        StowagePlan stowagePlan = stowagePlanService.getStowagePlanById(param.getStowagePlanId());

        CellSnapshot cellSnapshot = stowagePlanService.findCellSnapshot(
                stowagePlan, param.getBayIndex(), param.getRowIndex(), param.getTierIndex()
        );

        Container container = containerService.getContainer(param.getContainerNo());

        if (!cellSnapshot.getAllowReefer() && container.getIsReefer() != null && container.getIsReefer()) {
            throw new StowagePlanException(
                    "Cell does not allow reefer container: " + param.getContainerNo(),
                    Map.ofEntries(
                            Map.entry("containerNo", param.getContainerNo()),
                            Map.entry("bayIndex", param.getBayIndex()),
                            Map.entry("rowIndex", param.getRowIndex()),
                            Map.entry("tierIndex", cellSnapshot.getTierIndex())
                    )
            );
        }

        if (stowagePlanSlotRepo.isStowagePlanSlotOccupied(
                param.getBayIndex(), param.getRowIndex(), param.getTierIndex(),
                param.getOperationType(), param.getStowagePlanId()
        )) {
            throw new StowagePlanException(
                    "Stowage plan slot is already occupied for the given position and operation type.",
                    Map.ofEntries(
                            Map.entry("containerNo", param.getContainerNo()),
                            Map.entry("bayIndex", param.getBayIndex()),
                            Map.entry("rowIndex", param.getRowIndex()),
                            Map.entry("tierIndex", cellSnapshot.getTierIndex())
                    )
            );
        }
        Optional<StowagePlanSlot> optionalStowagePlanSlot = stowagePlanSlotRepo.getSlotByContainerNoAndOpType(
                param.getContainerNo(), param.getOperationType(), param.getStowagePlanId()
        );
        if (optionalStowagePlanSlot.isPresent()) {
            var slot = optionalStowagePlanSlot.get();
            throw buildContainerAlreadyPlannedException(
                    slot.getContainerSnapshot().getContainerNo(), slot.getOperationType(),
                    param.getBayIndex(), param.getRowIndex(), param.getTierIndex()
            );
        }

        StowagePlanSlotPosition position = new StowagePlanSlotPosition();
        position.setBayIndex(param.getBayIndex());
        position.setRowIndex(param.getRowIndex());
        position.setTierIndex(cellSnapshot.getTierIndex());

        StowagePlanSlot slot = new StowagePlanSlot();
        slot.setPlanId(param.getStowagePlanId());
        slot.setStowagePlanSlotPosition(position);
        slot.setContainerSnapshot(stowagePlanSnapshotDataMapper.toSnapshot(container));
        slot.setAllowReefer(cellSnapshot.getAllowReefer());
        slot.setOperationType(param.getOperationType());
        return stowagePlanSlotRepo.save(slot);
    }

    @Transactional
    public StowagePlanSlot updateStowagePlanSlot(UpdateStowagePlanSlotParam param) {

        Optional<StowagePlanSlot> optionalStowagePlanSlot = stowagePlanSlotRepo.getSlotByContainerNoAndOpType(
                param.getContainerNo(), param.getOperationType(), param.getStowagePlanId()
        );

        optionalStowagePlanSlot.ifPresent(stowagePlanSlot -> stowagePlanSlotRepo.delete(stowagePlanSlot));
        Optional<StowagePlanSlot> targetSlotOpt = stowagePlanSlotRepo.findByBayRowTierIndex(
                param.getBayIndex(), param.getRowIndex(), param.getTierIndex(), param.getOperationType(),
                param.getStowagePlanId()
        );

        targetSlotOpt.ifPresent(targetSlot ->
                stowagePlanSlotRepo.deleteSlot(targetSlot.getStowagePlanSlotPosition().getBayIndex(),
                        targetSlot.getStowagePlanSlotPosition().getRowIndex(),
                        targetSlot.getStowagePlanSlotPosition().getTierIndex(),
                        targetSlot.getOperationType(), targetSlot.getContainerSnapshot().getContainerNo(),
                        targetSlot.getPlanId()
                ));
        
        return this.addStowagePlanSlot(new AddStowagePlanSlotParam(
                param.getBayIndex(), param.getRowIndex(), param.getTierIndex(),
                param.getContainerNo(), param.getStowagePlanId(), param.getOperationType()
        ));
    }

    @Transactional
    public void deleteStowagePlanSlot(DeleteStowagePlanSlotParam param) {
        Optional<StowagePlanSlot> existingSlot = stowagePlanSlotRepo.findByBayRowTierIndex(
                param.getBayIndex(), param.getRowIndex(), param.getTierIndex(),
                param.getOperationType(), param.getStowagePlanId()
        );
        existingSlot.ifPresent(slot -> stowagePlanSlotRepo.delete(slot));
    }


    private StowagePlanException buildContainerAlreadyPlannedException(
            String containerNo, String operationType,
            Integer bayIndex, Integer rowIndex, Integer tierIndex
    ) {
        return new StowagePlanException(
                "Container is already planned in another slot for the given operation type. " +
                        "Properties are the container current location and operation type.",
                Map.ofEntries(
                        Map.entry("containerNo", containerNo),
                        Map.entry("bayIndex", bayIndex),
                        Map.entry("rowIndex", rowIndex),
                        Map.entry("tierIndex", tierIndex),
                        Map.entry("operationType", operationType)
                )
        );
    }

}
