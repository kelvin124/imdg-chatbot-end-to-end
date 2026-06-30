package com.example.demo.controller.api;

import com.example.demo.controller.api.dto.entity.stowage.*;
import com.example.demo.controller.api.dto.entity.stowage.slot.StowagePlanSlotDto;
import com.example.demo.controller.api.dto.entity.stowage.slot.StowagePlanSlotDtoMapper;
import com.example.demo.controller.api.request.stowage.CreateStowagePlanRequest;
import com.example.demo.controller.api.request.stowage.DeleteStowagePlanRequest;
import com.example.demo.controller.api.request.stowage.SyncVesselStructureRequest;
import com.example.demo.controller.api.response.stowage.CreateStowagePlanResponse;
import com.example.demo.controller.api.response.stowage.GetStowagePlanResponse;
import com.example.demo.controller.api.response.stowage.SyncVesselStructureResponse;
import com.example.demo.entity.stowage.BaySnapshot;
import com.example.demo.entity.stowage.StowagePlan;
import com.example.demo.entity.stowage.StowagePlanSnapshotDataMapper;
import com.example.demo.entity.stowage.TankSnapshot;
import com.example.demo.service.StowagePlanService;
import com.example.demo.service.param.CreateStowagePlanParam;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/stowage-plan")
public class StowagePlanControllerV1 {

    private final StowagePlanService stowagePlanService;
    private final StowagePlanDtoMapper stowagePlanDtoMapper;
    private final StowagePlanSlotDtoMapper stowagePlanSlotDtoMapper;

    @Autowired
    private StowagePlanSnapshotDataMapper stowagePlanSnapshotDataMapper;

    public StowagePlanControllerV1(
            @Autowired StowagePlanService stowagePlanService,
            @Autowired StowagePlanDtoMapper stowagePlanDtoMapper,
            @Autowired StowagePlanSlotDtoMapper stowagePlanSlotDtoMapper
    ) {
        this.stowagePlanService = stowagePlanService;
        this.stowagePlanDtoMapper = stowagePlanDtoMapper;
        this.stowagePlanSlotDtoMapper = stowagePlanSlotDtoMapper;
    }

    @GetMapping(path = "/{planId}/bay/has-reefer-cell", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> getStowagePlan(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex
    ) {
        Boolean hasReeferCell = stowagePlanService.checkIfBayHasReeferCell(planId, bayIndex);
        return ResponseEntity.ok(hasReeferCell);
    }

    @GetMapping(path = "/{planId}")
    public ResponseEntity<GetStowagePlanResponse> getStowagePlan(
            @PathVariable String planId,
            @RequestParam(name = "slots", required = false, defaultValue = "false") Boolean withSlots
    ) {
        GetStowagePlanResponse response = new GetStowagePlanResponse();
        StowagePlan stowagePlan = stowagePlanService.getStowagePlanById(planId);
        List<StowagePlanSlotDto> stowagePlanSlotDtoList = new ArrayList<>();
        if (withSlots != null && withSlots) {
            stowagePlanService.getStowagePlanSlotById(planId).forEach(stowagePlanSlot -> {
                stowagePlanSlotDtoList.add(
                        stowagePlanSlotDtoMapper.toDto(stowagePlanSlot)
                );
            });
            response.setStowagePlanSlot(stowagePlanSlotDtoList);
        }
        response.setStowagePlan(stowagePlanDtoMapper.toDto(stowagePlan));
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<Void> deleteStowagePlan(@Valid @RequestBody DeleteStowagePlanRequest request) {
        stowagePlanService.deleteStowagePlanById(request.getStowagePlanId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/new", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CreateStowagePlanResponse> createNewStowagePlanV1(@Valid @RequestBody CreateStowagePlanRequest request) {
        StowagePlan stowagePlan = stowagePlanService.createStowagePlan(
                new CreateStowagePlanParam(request.getName(), request.getVesselId())
        );
        StowagePlanDto dto = stowagePlanDtoMapper.toDto(stowagePlan);
        CreateStowagePlanResponse response = new CreateStowagePlanResponse(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/sync-vessel-structure", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<SyncVesselStructureResponse> syncVesselStructureV1(@Valid @RequestBody SyncVesselStructureRequest request) {
        StowagePlan stowagePlan = stowagePlanService.syncVesselStructure(request.getStowagePlanId());
        SyncVesselStructureResponse response = new SyncVesselStructureResponse();
        StowagePlanDto dto = stowagePlanDtoMapper.toDto(stowagePlan);
        response.setStowagePlanDto(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{planId}/tanks", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TankSnapshotDto>> getTanks(
            @PathVariable String planId
    ) {
        List<TankSnapshot> tankSnapshot = stowagePlanService.getTankSnapshot(
                planId
        );
        var dto = new ArrayList<TankSnapshotDto>();
        tankSnapshot.forEach(snapshot -> {
            dto.add(stowagePlanDtoMapper.toDto(snapshot));
        });
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "/{planId}/bay", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BaySnapshotDto> getStowagePlanBay(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex
    ) {
        BaySnapshot baySnapshot = stowagePlanService.getStowagePlanBaySnapshot(
                planId, bayIndex
        );
        return ResponseEntity.ok(stowagePlanDtoMapper.toDto(baySnapshot));
    }

    @GetMapping(path = "/{planId}/bay/count", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Integer> getStowagePlanBayCount(
            @PathVariable String planId
    ) {
        var baySnapshots = stowagePlanService.getStowagePlanById(planId).getBaySnapshots();
        if (baySnapshots == null || baySnapshots.isEmpty()) {
            return ResponseEntity.ok(0);
        }
        var count = stowagePlanService.getStowagePlanById(planId).getBaySnapshots().size();
        return ResponseEntity.ok(count);
    }

    @GetMapping(path = "/{planId}/bay/exist", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> checkIfBayExists(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex
    ) {
        return ResponseEntity.ok(
                stowagePlanService.checkIfBayExists(planId, bayIndex)
        );
    }


    @GetMapping(path = "/{planId}/bay/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<BaySnapshotDto>> getAllStowagePlanBay(
            @PathVariable String planId
    ) {
        List<BaySnapshot> baySnapshot = stowagePlanService.getStowagePlanBaySnapshot(planId);
        var dtoList = new ArrayList<BaySnapshotDto>();
        baySnapshot.forEach(snapshot -> {
            dtoList.add(stowagePlanDtoMapper.toDto(snapshot));
        });
        return ResponseEntity.ok(dtoList.stream().filter(BaySnapshotDto::hasCell).toList());
    }

    @GetMapping(path = "/{planId}/row", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RowSnapshotDto> getStowagePlanRow(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex,
            @RequestParam(name = "rowIndex") Integer rowIndex
    ) {
        var snapshot = stowagePlanService.getStowagePlanRowSnapshot(
                planId, bayIndex, rowIndex
        );
        return ResponseEntity.ok(stowagePlanDtoMapper.toDto(snapshot));
    }

    @GetMapping(path = "/{planId}/row/exist", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> checkIfBayRowExists(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex,
            @RequestParam(name = "rowIndex") Integer rowIndex
    ) {
        var exists = stowagePlanService.checkIfRowExists(
                planId, bayIndex, rowIndex
        );
        return ResponseEntity.ok(exists);
    }

    @GetMapping(path = "/{planId}/row/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<RowSnapshotDto>> getAllStowagePlanRow(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex
    ) {
        var rowSnapshots = stowagePlanService.getStowagePlanRowSnapshot(planId, bayIndex);
        var dtoList = new ArrayList<RowSnapshotDto>();
        rowSnapshots.forEach(snapshot -> {
            dtoList.add(stowagePlanDtoMapper.toDto(snapshot));
        });
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(path = "/{planId}/tier", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CellSnapshotDto> getStowagePlanTier(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex,
            @RequestParam(name = "rowIndex") Integer rowIndex,
            @RequestParam(name = "tierIndex") Integer tierIndex
    ) {
        var cellSnapshot = stowagePlanService.getStowagePlanCellSnapshot(
                planId, bayIndex, rowIndex, tierIndex
        );
        return ResponseEntity.ok(stowagePlanDtoMapper.toDto(cellSnapshot));
    }

    @GetMapping(path = "/{planId}/tier/all", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<CellSnapshotDto>> getAllStowagePlanTier(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex,
            @RequestParam(name = "rowIndex") Integer rowIndex
    ) {
        var cellSnapshot = stowagePlanService.getStowagePlanCellSnapshot(
                planId, bayIndex, rowIndex
        );
        var dtoList = new ArrayList<CellSnapshotDto>();
        cellSnapshot.forEach(snapshot -> {
            dtoList.add(stowagePlanDtoMapper.toDto(snapshot));
        });
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(path = "/{planId}/tier/exists", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> checkIfBayRowTierExists(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex,
            @RequestParam(name = "rowIndex") Integer rowIndex,
            @RequestParam(name = "tierIndex") Integer tierIndex
    ) {
        var exist = stowagePlanService.checkIfTierExists(
                planId, bayIndex, rowIndex, tierIndex
        );
        return ResponseEntity.ok(exist);
    }

    @GetMapping(path = "/{planId}/vessel-profile", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<VesselProfileSnapshotDto> getShipInfo(
            @PathVariable String planId
    ) {
        var snapshot = stowagePlanService.getStowagePlanById(planId).getVesselProfileSnapshot();
        return ResponseEntity.ok(
                stowagePlanDtoMapper.toDto(snapshot)
        );
    }

}