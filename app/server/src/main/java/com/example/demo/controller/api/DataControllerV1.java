package com.example.demo.controller.api;

import com.example.demo.controller.api.dto.entity.imdg.HazardClassDefinitionDto;
import com.example.demo.controller.api.dto.entity.imdg.HazardClassDefinitionDtoMapper;
import com.example.demo.controller.api.dto.entity.imdg.HazardDivisionDefinitionDto;
import com.example.demo.controller.api.dto.entity.imdg.HazardDivisionDefinitionDtoMapper;
import com.example.demo.entity.imdg.*;
import com.example.demo.service.ContainerGenerationService;
import com.example.demo.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
public class DataControllerV1 {

    @Autowired
    private DataService dataService;

    @Autowired
    private ContainerGenerationService containerGenerationService;

    @Autowired
    private HazardClassDefinitionDtoMapper hazardClassDefinitionDtoMapper;

    @Autowired
    private HazardDivisionDefinitionDtoMapper hazardDivisionDefinitionDtoMapper;

    @PostMapping("/imdg/import/compatibility-groups")
    public ResponseEntity<List<CompatibilityGroup>> importCompatibilityGroups() throws IOException {
        List<CompatibilityGroup> result = dataService.importCompatibilityGroups();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/imdg/import/segregation-rules")
    public ResponseEntity<List<SegregationRule>> importIMDGSegregation() throws IOException {
        List<SegregationRule> result = dataService.importIMDGSegregation();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/imdg/import/segregation-rule-codes")
    public ResponseEntity<List<SegregationRuleCode>> importIMDGSegregationRequirementCodes() throws IOException {
        List<SegregationRuleCode> result = dataService.importIMDGSegregationRequirementCodes();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/imdg/import/segregation-codes")
    public ResponseEntity<List<SegregationCode>> importSegregationCodes() throws IOException {
        List<SegregationCode> result = dataService.importSegregationCodes();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/imdg/import/segregation-group-codes")
    public ResponseEntity<List<SegregationGroupCode>> importSegregationGroupCodes() throws IOException {
        List<SegregationGroupCode> result = dataService.importSegregationGroupCodes();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/imdg/import/hazard-division-definition")
    public ResponseEntity<List<HazardDivisionDefinitionDto>> importDivisionDefinition() throws IOException {
        List<HazardDivisionDefinition> result = dataService.importDivisionDefinition();
        return ResponseEntity.ok(result.stream().map(hazardDivisionDefinitionDtoMapper::toDto).toList());
    }

    @PostMapping("/imdg/import/hazard-class-definition")
    public ResponseEntity<List<HazardClassDefinitionDto>> importHazardClassDefinition() throws IOException {
        List<HazardClassDefinition> result = dataService.importHazardClassDefinitions();
        return ResponseEntity.ok(result.stream().map(hazardClassDefinitionDtoMapper::toDto).toList());
    }

    @PostMapping(path = "/imdg/import/dg", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> importDangerousGoods() {
        try {
            int count = dataService.importDangerousGoods();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Dangerous goods data imported successfully");
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to import dangerous goods data");
            error.put("error", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping(path = "/vessel/import", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> importFromSeed() {
        try {
            DataService.ImportResult result = dataService.importAll();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Data imported successfully");
            response.put("total", result.getTotal());
            response.put("vesselProfiles", result.getVesselProfiles());
            response.put("bays", result.getBays());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Failed to import data");
            error.put("error", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping(path = "/container/generate")
    public ResponseEntity<Void> generateContainerData(@RequestParam("count") int count) {
        try {
            containerGenerationService.generateAndSave(count);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}