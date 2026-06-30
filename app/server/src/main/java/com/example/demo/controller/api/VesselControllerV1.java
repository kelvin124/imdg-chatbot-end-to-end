package com.example.demo.controller.api;

import com.example.demo.controller.api.dto.entity.vessel.structure.BayDto;
import com.example.demo.controller.api.dto.entity.vessel.structure.CellDto;
import com.example.demo.controller.api.dto.entity.vessel.structure.RowDto;
import com.example.demo.controller.api.dto.entity.vessel.structure.VesselStructureDtoMapper;
import com.example.demo.service.VesselStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/vessel")
public class VesselControllerV1 {

    private final VesselStructureService vesselStructureService;
    private final VesselStructureDtoMapper vesselStructureDtoMapper;

    public VesselControllerV1(
            @Autowired VesselStructureService vesselStructureService,
            @Autowired VesselStructureDtoMapper vesselStructureDtoMapper
    ) {
        this.vesselStructureService = vesselStructureService;
        this.vesselStructureDtoMapper = vesselStructureDtoMapper;
    }

    @GetMapping(path = "/bay", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BayDto> getBay(
            @RequestParam("vesselId") String vesselId,
            @RequestParam("bayIndex") int bayIndex
    ) {
        return vesselStructureService.getBay(vesselId, bayIndex)
                .map(bay -> ResponseEntity.ok(vesselStructureDtoMapper.toBayDto(bay)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/row", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<RowDto> getRow(
            @RequestParam("vesselId") String vesselId,
            @RequestParam("bayIndex") int bayIndex,
            @RequestParam("rowIndex") int rowIndex
    ) {
        return vesselStructureService.getRow(vesselId, bayIndex, rowIndex)
                .map(row ->
                        ResponseEntity.ok(vesselStructureDtoMapper.toRowDto(row))
                ).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/cell", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CellDto> getCell(
            @RequestParam("vesselId") String vesselId,
            @RequestParam("bayIndex") int bayIndex,
            @RequestParam("rowIndex") int rowIndex,
            @RequestParam("tierIndex") int tierIndex,
            @RequestParam("bayType") String bayType
    ) {
        return vesselStructureService.getCell(vesselId, bayIndex, rowIndex, tierIndex, bayType)
                .map(cell ->
                        ResponseEntity.ok().body(vesselStructureDtoMapper.toCellDto(cell))
                ).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(path = "/cell/reefer", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Boolean> isCellReefer(
            @RequestParam("vesselId") String vesselId,
            @RequestParam("bayIndex") int bayIndex,
            @RequestParam("rowIndex") int rowIndex,
            @RequestParam("tierIndex") int tierIndex,
            @RequestParam("bayType") String bayType
    ) {
        boolean allowReefer = vesselStructureService.allowReefer(vesselId, bayIndex, rowIndex, tierIndex, bayType);
        return ResponseEntity.ok(allowReefer);
    }

}