package com.example.demo.controller.api;

import com.example.demo.controller.api.dto.entity.imdg.*;
import com.example.demo.entity.imdg.DangerousGoods;
import com.example.demo.entity.imdg.HazardClassDefinition;
import com.example.demo.entity.imdg.HazardDivisionDefinition;
import com.example.demo.service.IMDGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/imdg")
public class IMDGControllerV1 {

    @Autowired
    private IMDGService IMDGService;

    @Autowired
    private DangerousGoodsDtoMapper dangerousGoodsDtoMapper;

    @Autowired
    private CompatibilityGroupDtoMapper compatibilityGroupDtoMapper;

    @Autowired
    private SegregationRuleDtoMapper segregationRuleDtoMapper;

    @Autowired
    private SegregationRuleCodeDtoMapper segregationRuleCodeDtoMapper;

    @Autowired
    private SegregationCodeDtoMapper segregationCodeDtoMapper;

    @Autowired
    private SegregationGroupCodeDtoMapper segregationGroupCodeDtoMapper;

    @Autowired
    private HazardClassDefinitionDtoMapper hazardClassDefinitionDtoMapper;

    @Autowired
    private HazardDivisionDefinitionDtoMapper hazardDivisionDefinitionDtoMapper;

    @PostMapping("/class/batch")
    public ResponseEntity<List<DangerousGoodsFullDto>> getDangerousGoodsByClass(
            @RequestBody List<String> dgClasses, @RequestParam(required = false, defaultValue = "1") Integer sampleCount
    ) {
        var totalSample = sampleCount * dgClasses.size();
        var fullDangerousGoodsList = IMDGService.getDangerousGoodsByHazardClass(dgClasses);
        var dangerousGoodsList = new ArrayList<DangerousGoods>();
        var sampledSet = new HashSet<String>();
        for (DangerousGoods item : fullDangerousGoodsList) {
            if (!sampledSet.contains(item.getDivision())) {
                dangerousGoodsList.add(item);
                sampledSet.add(item.getDivision());
            }
            if (totalSample != dangerousGoodsList.size()) {
                dangerousGoodsList.add(item);
            } else {
                break;
            }
        }
        var allSegregationCodes = dangerousGoodsList.stream().flatMap(item -> item.getSegregation().stream()).toList();
        var segregationCodes = IMDGService.getSegregationCodes(allSegregationCodes);
        var segregationGroupCodes = IMDGService.getSegregationGroupCodes(allSegregationCodes);
        var hazardClassDef = IMDGService.getHazardClassDefinitions().stream()
                .collect(Collectors.toMap(HazardClassDefinition::getHazardClass, Function.identity()));
        var hazardDivisionDef = IMDGService.getHazardDivisionDefinitions().stream()
                .collect(Collectors.toMap(HazardDivisionDefinition::getDivision, Function.identity()));
        return ResponseEntity.ok().body(
                dangerousGoodsList.stream().map(item ->
                        dangerousGoodsDtoMapper.toDto(
                                item,
                                segregationCodes.stream().map(segregationCodeDtoMapper::toDto).toList(),
                                segregationGroupCodes.stream().map(segregationGroupCodeDtoMapper::toDto).toList(),
                                hazardClassDef.get(item.getDgClass()), hazardDivisionDef.get(item.getDivision())
                        )
                ).toList()
        );
    }

    @PostMapping("/division/batch")
    public ResponseEntity<List<DangerousGoodsFullDto>> getDangerousGoodsByDivisions(
            @RequestBody List<String> divisions, @RequestParam(required = false, defaultValue = "1") Integer sampleCount
    ) {
        var totalSample = sampleCount * divisions.size();
        var fullDangerousGoodsList = IMDGService.getDangerousGoodsByDivision(divisions);
        var dangerousGoodsList = new ArrayList<DangerousGoods>();
        var sampledSet = new HashSet<String>();
        for (DangerousGoods item : fullDangerousGoodsList) {
            if (!sampledSet.contains(item.getDivision())) {
                dangerousGoodsList.add(item);
                sampledSet.add(item.getDivision());
            }
            if (sampledSet.size() == divisions.size()) {
                dangerousGoodsList.add(item);
            }
            if (totalSample == dangerousGoodsList.size()) {
                break;
            }
        }
        var allSegregationCodes = dangerousGoodsList.stream().flatMap(item -> item.getSegregation().stream()).toList();
        var segregationCodes = IMDGService.getSegregationCodes(allSegregationCodes);
        var segregationGroupCodes = IMDGService.getSegregationGroupCodes(allSegregationCodes);
        var hazardClassDef = IMDGService.getHazardClassDefinitions().stream()
                .collect(Collectors.toMap(HazardClassDefinition::getHazardClass, Function.identity()));
        var hazardDivisionDef = IMDGService.getHazardDivisionDefinitions().stream()
                .collect(Collectors.toMap(HazardDivisionDefinition::getDivision, Function.identity()));
        return ResponseEntity.ok().body(
                dangerousGoodsList.stream().map(item ->
                        dangerousGoodsDtoMapper.toDto(
                                item,
                                segregationCodes.stream().map(segregationCodeDtoMapper::toDto).toList(),
                                segregationGroupCodes.stream().map(segregationGroupCodeDtoMapper::toDto).toList(),
                                hazardClassDef.get(item.getDgClass()), hazardDivisionDef.get(item.getDivision())
                        )
                ).toList()
        );
    }

    @GetMapping("/{unNo}")
    public ResponseEntity<DangerousGoodsFullDto> getDangerousGoods(@PathVariable String unNo) {
        var dangerousGoods = IMDGService.findByUnNo(unNo);
        var allSegregationCodes = dangerousGoods.getSegregation();
        var segregationCodes = IMDGService.getSegregationCodes(allSegregationCodes);
        var segregationGroupCodes = IMDGService.getSegregationGroupCodes(allSegregationCodes);
        var hazardClassDef = IMDGService.getHazardClassDefinitions().stream()
                .collect(Collectors.toMap(HazardClassDefinition::getHazardClass, Function.identity()));
        var hazardDivisionDef = IMDGService.getHazardDivisionDefinitions().stream()
                .collect(Collectors.toMap(HazardDivisionDefinition::getDivision, Function.identity()));
        return ResponseEntity.ok().body(
                dangerousGoodsDtoMapper.toDto(
                        dangerousGoods,
                        segregationCodes.stream().map(segregationCodeDtoMapper::toDto).toList(),
                        segregationGroupCodes.stream().map(segregationGroupCodeDtoMapper::toDto).toList(),
                        hazardClassDef.get(dangerousGoods.getDgClass()), hazardDivisionDef.get(dangerousGoods.getDivision())
                )
        );
    }

    @PostMapping("/no/batch")
    public ResponseEntity<List<DangerousGoodsFullDto>> getDangerousGoodsFull(@RequestBody List<String> unNos) {

        var dangerousGoods = IMDGService.findByUnNos(unNos);
        var allSegregationCodes = dangerousGoods.stream().flatMap(item -> item.getSegregation().stream()).toList();
        var segregationCodes = IMDGService.getSegregationCodes(allSegregationCodes);
        var segregationGroupCodes = IMDGService.getSegregationGroupCodes(allSegregationCodes);
        var hazardClassDef = IMDGService.getHazardClassDefinitions().stream()
                .collect(Collectors.toMap(HazardClassDefinition::getHazardClass, Function.identity()));
        var hazardDivisionDef = IMDGService.getHazardDivisionDefinitions().stream()
                .collect(Collectors.toMap(HazardDivisionDefinition::getDivision, Function.identity()));
        var resultDto = new ArrayList<DangerousGoodsFullDto>();
        dangerousGoods.forEach(item -> {
            resultDto.add(
                    dangerousGoodsDtoMapper.toDto(
                            item,
                            segregationCodes.stream().map(segregationCodeDtoMapper::toDto).toList(),
                            segregationGroupCodes.stream().map(segregationGroupCodeDtoMapper::toDto).toList(),
                            hazardClassDef.get(item.getDgClass()), hazardDivisionDef.get(item.getDivision())
                    )
            );
        });
        return ResponseEntity.ok().body(resultDto);
    }

    @GetMapping("/compatibility-group/{code}")
    public ResponseEntity<CompatibilityGroupDto> getCompatibilityGroup(@PathVariable String code) {
        var entity = IMDGService.getCompatibilityGroupByCode(code);
        return ResponseEntity.ok().body(
                compatibilityGroupDtoMapper.toDto(entity)
        );
    }

    @PostMapping("/compatibility-group/batch")
    public ResponseEntity<List<CompatibilityGroupDto>> getCompatibilityGroups(@RequestBody List<String> compatibilityGroups) {
        var entities = IMDGService.findByCompatibilityGroups(compatibilityGroups);
        return ResponseEntity.ok().body(
                entities.stream().map(compatibilityGroupDtoMapper::toDto).toList()
        );
    }


    @GetMapping("/compatibility-group/all")
    public ResponseEntity<List<CompatibilityGroupDto>> getAllCompatibilityGroups() {
        var entities = IMDGService.getAllCompatibilityGroups();
        return ResponseEntity.ok().body(
                entities.stream().map(compatibilityGroupDtoMapper::toDto).toList()
        );
    }

    @GetMapping("/segregation-rule/class/{hazardClass}")
    public ResponseEntity<List<SegregationRuleDto>> getSegregationRuleByClass(@PathVariable String hazardClass) {
        var rules = IMDGService.getSegregationRuleByHazardClass(hazardClass);
        var ruleCodeDefinition = IMDGService.getSegregationRuleCode();
        var dtoList = new ArrayList<SegregationRuleDto>();
        rules.forEach(segregationRule -> {
            dtoList.add(segregationRuleDtoMapper.toDto(segregationRule, ruleCodeDefinition));
        });
        return ResponseEntity.ok().body(dtoList);
    }

    @PostMapping("/segregation-rule/class/batch")
    public ResponseEntity<List<SegregationRuleDto>> getBatchSegregationRuleByClass(@RequestBody List<String> hazardClasses) {
        var rules = IMDGService.getSegregationRuleByHazardClass(hazardClasses);
        var ruleCodeDefinition = IMDGService.getSegregationRuleCode();
        var dtoList = new ArrayList<SegregationRuleDto>();
        rules.forEach(segregationRule -> {
            dtoList.add(segregationRuleDtoMapper.toDto(segregationRule, ruleCodeDefinition));
        });
        return ResponseEntity.ok().body(dtoList);
    }


    @GetMapping("/segregation-rule/class/all")
    public ResponseEntity<List<SegregationRuleDto>> getAllSegregationRuleByClass() {
        var rules = IMDGService.getAllSegregationRuleByHazardClass();
        var ruleCodeDefinition = IMDGService.getSegregationRuleCode();
        var dtoList = new ArrayList<SegregationRuleDto>();
        rules.forEach(segregationRule -> {
            dtoList.add(segregationRuleDtoMapper.toDto(segregationRule, ruleCodeDefinition));
        });
        return ResponseEntity.ok().body(dtoList);
    }


    @GetMapping("/segregation-rule/division/{division}")
    public ResponseEntity<SegregationRuleDto> getSegregationRuleByDivision(@PathVariable String division) {
        var rule = IMDGService.getSegregationRuleByDivision(division);
        var ruleCodeDefinition = IMDGService.getSegregationRuleCode();
        return ResponseEntity.ok().body(
                segregationRuleDtoMapper.toDto(rule, ruleCodeDefinition)
        );
    }

    @PostMapping("/segregation-rule/division/batch")
    public ResponseEntity<List<SegregationRuleDto>> getBatchSegregationRuleByDivision(@RequestBody List<String> divisions) {
        var rules = IMDGService.getSegregationRuleByDivision(divisions);
        var ruleCodeDefinition = IMDGService.getSegregationRuleCode();
        var dtoList = new ArrayList<SegregationRuleDto>();
        rules.forEach(segregationRule -> {
            dtoList.add(segregationRuleDtoMapper.toDto(segregationRule, ruleCodeDefinition));
        });
        return ResponseEntity.ok().body(dtoList);
    }

    @GetMapping("/segregation-rule-code/{code}")
    public ResponseEntity<SegregationRuleCodeDto> getIMDGSegregationRequirementCode(@PathVariable String code) {
        var entity = IMDGService.getSegregationRequirementCode(code);
        return ResponseEntity.ok().body(
                segregationRuleCodeDtoMapper.toDto(entity)
        );
    }

    @PostMapping("/segregation-rule-code/batch")
    public ResponseEntity<List<SegregationRuleCodeDto>> getIMDGSegregationRequirementCodeBatch(@RequestBody List<String> codes) {
        var entities = IMDGService.getSegregationRequirementCodes(codes);
        return ResponseEntity.ok().body(
                entities.stream().map(segregationRuleCodeDtoMapper::toDto).toList()
        );
    }


    @GetMapping("/segregation-rule-code/all")
    public ResponseEntity<List<SegregationRuleCodeDto>> getAllIMDGSegregationRequirementCode() {
        var entities = IMDGService.getAllSegregationRequirementCodes();
        return ResponseEntity.ok().body(
                entities.stream().map(segregationRuleCodeDtoMapper::toDto).toList()
        );
    }

    @GetMapping("/segregation-code/{code}")
    public ResponseEntity<SegregationCodeDto> getSegregationCode(@PathVariable String code) {
        var entity = IMDGService.getSegregationCode(code);
        return ResponseEntity.ok().body(
                segregationCodeDtoMapper.toDto(entity)
        );
    }

    @PostMapping("/segregation-code/batch")
    public ResponseEntity<List<SegregationCodeDto>> getSegregationCodeBatch(@RequestBody List<String> codes) {
        var entities = IMDGService.getSegregationCodes(codes);
        return ResponseEntity.ok().body(
                entities.stream().map(segregationCodeDtoMapper::toDto).toList()
        );
    }


    @GetMapping("/segregation-code/all")
    public ResponseEntity<List<SegregationCodeDto>> getAllSegregationCode() {
        var entities = IMDGService.getAllSegregationCodes();
        return ResponseEntity.ok().body(
                entities.stream().map(segregationCodeDtoMapper::toDto).toList()
        );
    }

    @GetMapping("/segregation-group-code/{code}")
    public ResponseEntity<SegregationGroupCodeDto> getSegregationGroupCode(@PathVariable String code) {
        var entity = IMDGService.getSegregationGroupCode(code);
        return ResponseEntity.ok().body(
                segregationGroupCodeDtoMapper.toDto(entity)
        );
    }

    @PostMapping("/segregation-group-code/batch")
    public ResponseEntity<List<SegregationGroupCodeDto>> getSegregationGroupCodeBatch(@RequestBody List<String> codes) {
        var entities = IMDGService.getSegregationGroupCodes(codes);
        return ResponseEntity.ok().body(
                entities.stream().map(segregationGroupCodeDtoMapper::toDto).toList()
        );
    }


    @GetMapping("/segregation-group-code/all")
    public ResponseEntity<List<SegregationGroupCodeDto>> getAllSegregationGroupCodeBatch() {
        var entities = IMDGService.getAllSegregationGroupCodes();
        return ResponseEntity.ok().body(
                entities.stream().map(segregationGroupCodeDtoMapper::toDto).toList()
        );
    }


    @GetMapping("/hazard-class-definition")
    public ResponseEntity<List<HazardClassDefinitionDto>> getDivisionDefinitions() {
        var entities = IMDGService.getHazardClassDefinitions();
        return ResponseEntity.ok().body(
                entities.stream().map(hazardClassDefinitionDtoMapper::toDto).toList()
        );
    }

    @GetMapping("/hazard-division-definition")
    public ResponseEntity<List<HazardDivisionDefinitionDto>> getHazardClassDefinitions() {
        var entities = IMDGService.getHazardDivisionDefinitions();
        return ResponseEntity.ok().body(
                entities.stream().map(hazardDivisionDefinitionDtoMapper::toDto).toList()
        );
    }
}