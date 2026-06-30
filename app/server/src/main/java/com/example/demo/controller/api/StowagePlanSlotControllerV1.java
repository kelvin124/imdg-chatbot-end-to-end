package com.example.demo.controller.api;

import com.example.demo.controller.api.dto.entity.stowage.StowagePlanDtoMapper;
import com.example.demo.controller.api.dto.entity.stowage.slot.StowagePlanSlotDto;
import com.example.demo.controller.api.dto.entity.stowage.slot.StowagePlanSlotDtoMapper;
import com.example.demo.controller.api.request.stowage.AddStowagePlanSlotRequest;
import com.example.demo.controller.api.request.stowage.DeleteStowagePlanSlotRequest;
import com.example.demo.controller.api.request.stowage.UpdateStowagePlanSlotRequest;
import com.example.demo.controller.api.response.stowage.AddStowagePlanSlotResponse;
import com.example.demo.controller.api.response.stowage.GetStowagePlanSlotResponse;
import com.example.demo.controller.api.response.stowage.UpdateStowagePlanSlotResponse;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.service.StowagePlanService;
import com.example.demo.service.StowagePlanSlotService;
import com.example.demo.service.param.AddStowagePlanSlotParam;
import com.example.demo.service.param.DeleteStowagePlanSlotParam;
import com.example.demo.service.param.GetStowagePlanSlotParam;
import com.example.demo.service.param.UpdateStowagePlanSlotParam;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/stowage-plan-slot")
public class StowagePlanSlotControllerV1 {

    @Autowired
    private StowagePlanService stowagePlanService;

    @Autowired
    private StowagePlanSlotService stowagePlanSlotService;

    @Autowired
    private StowagePlanDtoMapper stowagePlanDtoMapper;

    @Autowired
    private StowagePlanSlotDtoMapper stowagePlanSlotDtoMapper;

    @GetMapping(path = "/{planId}/bay/{bayIndex}/row/{rowIndex}/tier/{tierIndex}/is-free", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> checkIfCellFree(
            @PathVariable String planId,
            @PathVariable Integer bayIndex,
            @PathVariable Integer rowIndex,
            @PathVariable(name = "tierIndex") Integer tierIndex,
            @RequestParam(name = "opType") String operationType
    ) {
        var isFree = stowagePlanSlotService.checkIfStowagePlanSlotOccupied(
                planId, bayIndex, rowIndex, tierIndex, operationType
        );
        return ResponseEntity.ok(isFree);
    }

    @GetMapping(path = "/{planId}/bay/{bayIndex}/row/{rowIndex}/tier", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GetStowagePlanSlotResponse> getStowagePlan(
            @PathVariable String planId,
            @PathVariable Integer bayIndex,
            @PathVariable Integer rowIndex,
            @RequestParam(name = "tierIndex") Integer tierIndex,
            @RequestParam(name = "opType") String operationType
    ) {
        StowagePlanSlot stowagePlanSlot = stowagePlanSlotService.getStowagePlanSlot(
                new GetStowagePlanSlotParam(planId, operationType, bayIndex, rowIndex, tierIndex)
        );
        GetStowagePlanSlotResponse response = new GetStowagePlanSlotResponse();
        response.setStowagePlanSlotDto(stowagePlanSlotDtoMapper.toDto(stowagePlanSlot));
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{planId}/bay", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<StowagePlanSlotDto>> getStowagePlan(
            @PathVariable String planId,
            @RequestParam(name = "bayIndex") Integer bayIndex,
            @RequestParam(name = "opType") String opType
    ) {
        List<StowagePlanSlot> stowagePlanSlot = stowagePlanSlotService.getStowagePlanSlotByBay(
                new GetStowagePlanSlotParam(planId, opType, bayIndex, null, null)
        );
        var dtoList = new ArrayList<StowagePlanSlotDto>();
        stowagePlanSlot.forEach(slot -> dtoList.add(stowagePlanSlotDtoMapper.toDto(slot)));
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(path = "/{planId}/bay/{bayIndex}/row", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<StowagePlanSlotDto>> getStowagePlan(
            @PathVariable String planId,
            @PathVariable Integer bayIndex,
            @RequestParam(name = "rowIndex") Integer rowIndex,
            @RequestParam(name = "opType") String opType
    ) {
        List<StowagePlanSlot> stowagePlanSlot = stowagePlanSlotService.getStowagePlanSlotByBayRow(
                new GetStowagePlanSlotParam(planId, opType, bayIndex, rowIndex, null)
        );
        var dtoList = new ArrayList<StowagePlanSlotDto>();
        stowagePlanSlot.forEach(slot -> dtoList.add(stowagePlanSlotDtoMapper.toDto(slot)));
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<AddStowagePlanSlotResponse> addStowagePlanSlotV1(@Valid @RequestBody AddStowagePlanSlotRequest request) {
        StowagePlanSlot newSlot = stowagePlanSlotService.addStowagePlanSlot(new AddStowagePlanSlotParam(request));
        AddStowagePlanSlotResponse response = new AddStowagePlanSlotResponse();
        response.setStowagePlanId(newSlot.getPlanId());
        response.setStowagePlanSlotDto(stowagePlanSlotDtoMapper.toDto(newSlot));
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/delete", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteStowagePlanSlotV1(@Valid @RequestBody DeleteStowagePlanSlotRequest request) {
        stowagePlanSlotService.deleteStowagePlanSlot(new DeleteStowagePlanSlotParam(request));
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UpdateStowagePlanSlotResponse> updateStowagePlanSlotV1(@Valid @RequestBody UpdateStowagePlanSlotRequest request) {
        StowagePlanSlot updatedSlot = stowagePlanSlotService.updateStowagePlanSlot(new UpdateStowagePlanSlotParam(request));
        UpdateStowagePlanSlotResponse response = new UpdateStowagePlanSlotResponse();
        response.setStowagePlanId(updatedSlot.getPlanId());
        response.setStowagePlanSlotDto(stowagePlanSlotDtoMapper.toDto(updatedSlot));
        return ResponseEntity.ok(response);
    }

}
