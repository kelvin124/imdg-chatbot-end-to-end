package com.example.demo.service;

import com.example.demo.entity.imdg.*;
import com.example.demo.repository.imdg.*;
import com.example.demo.service.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IMDGService {

    @Autowired
    private DangerousGoodsRepo dangerousGoodsRepo;

    @Autowired
    private CompatibilityGroupRepository compatibilityGroupRepository;

    @Autowired
    private SegregationRuleRepository segregationRuleRepository;

    @Autowired
    private SegregationRuleCodeRepository segregationRuleCodeRepository;

    @Autowired
    private SegregationCodeRepository segregationCodeRepository;

    @Autowired
    private SegregationGroupCodeRepository segregationGroupCodeRepository;

    @Autowired
    private HazardClassDefinitionRepository hazardClassDefinitionRepository;

    @Autowired
    private HazardDivisionDefinitionRepository hazardDivisionDefinitionRepository;

    public Map<String, String> getSegregationRuleCode() {
        return segregationRuleCodeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        SegregationRuleCode::getCode,
                        SegregationRuleCode::getDescription
                ));
    }

    public List<DangerousGoods> getDangerousGoodsByHazardClass(List<String> dgClasses) {
        return dangerousGoodsRepo.findByClassesIn(dgClasses);
    }

    public List<DangerousGoods> getDangerousGoodsByDivision(List<String> divisions) {
        return dangerousGoodsRepo.findByDivisionIn(divisions);
    }

    public DangerousGoods findByUnNo(String unNo) {
        return dangerousGoodsRepo.findByUnNo(unNo).orElseThrow(
                () -> new RecordNotFoundException("Dangerous goods with UN No " + unNo + " not found")
        );
    }

    public List<DangerousGoods> findByUnNos(List<String> unNos) {
        if (unNos == null || unNos.isEmpty()) {
            return List.of();
        }

        var dangerousGoodsList = dangerousGoodsRepo.findByUnNoIn(unNos);
        Map<String, DangerousGoods> dangerousGoodsMap = new HashMap<>();
        dangerousGoodsList.forEach(item -> dangerousGoodsMap.put(item.getUnNo(), item));

        return unNos.stream().map(unNo -> {
            var dangerousGoods = dangerousGoodsMap.get(unNo);
            if (dangerousGoods == null) {
                throw new RecordNotFoundException("Dangerous goods with UN No " + unNo + " not found");
            }
            return dangerousGoods;
        }).toList();
    }

    public List<CompatibilityGroup> getAllCompatibilityGroups() {
        return compatibilityGroupRepository.findAll();
    }

    public List<CompatibilityGroup> findByCompatibilityGroups(List<String> compatibilityGroups) {
        if (compatibilityGroups == null || compatibilityGroups.isEmpty()) {
            return List.of();
        }
        return compatibilityGroupRepository.findByCodeIn(compatibilityGroups);
    }

    public List<SegregationRuleCode> getAllSegregationRequirementCodes() {
        return segregationRuleCodeRepository.findAll();
    }

    public List<SegregationRuleCode> getSegregationRequirementCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return segregationRuleCodeRepository.findByCodeIn(codes);
    }

    public List<SegregationCode> getSegregationCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return segregationCodeRepository.findByCodeIn(codes);
    }

    public List<SegregationCode> getAllSegregationCodes() {
        return segregationCodeRepository.findAll();
    }

    public List<SegregationGroupCode> getSegregationGroupCodes(List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return segregationGroupCodeRepository.findByCodeIn(codes);
    }

    public List<SegregationGroupCode> getAllSegregationGroupCodes() {
        return segregationGroupCodeRepository.findAll();
    }

    public List<HazardClassDefinition> getHazardClassDefinitions() {
        return hazardClassDefinitionRepository.findAll();
    }

    public List<HazardDivisionDefinition> getHazardDivisionDefinitions() {
        return hazardDivisionDefinitionRepository.findAll();
    }

    public List<SegregationRule> getSegregationRuleByHazardClass(String hazardClass) {
        return segregationRuleRepository.findByDivisionIn(
                resolveDivisionsForHazardClass(hazardClass)
        );
    }

    public List<SegregationRule> getAllSegregationRuleByHazardClass() {
        return segregationRuleRepository.findAll();
    }

    public List<SegregationRule> getSegregationRuleByHazardClass(List<String> hazardClasses) {
        return segregationRuleRepository.findByDivisionIn(
                resolveDivisionsForHazardClass(hazardClasses)
        );
    }

    public SegregationRule getSegregationRuleByDivision(String division) {
        return segregationRuleRepository.findByDivision(division).orElseThrow(
                () -> new RecordNotFoundException("IMDG segregation for division " + division + " not found")
        );
    }

    public List<SegregationRule> getSegregationRuleByDivision(List<String> divisions) {
        if (divisions == null || divisions.isEmpty()) {
            return List.of();
        }

        return segregationRuleRepository.findAll().stream()
                .filter(rule -> divisions.contains(rule.getDivision()))
                .toList();
    }

    private List<String> resolveDivisionsForHazardClass(String hazardClass) {
        var definitionOpt = hazardDivisionDefinitionRepository.findByHazardClass(hazardClass);
        return definitionOpt.stream()
                .map(HazardDivisionDefinition::getDivision)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> resolveDivisionsForHazardClass(List<String> hazardClasses) {
        if (hazardClasses == null || hazardClasses.isEmpty()) {
            return List.of();
        }

        return hazardDivisionDefinitionRepository.findByHazardClassIn(hazardClasses).stream()
                .map(HazardDivisionDefinition::getDivision)
                .distinct()
                .collect(Collectors.toList());
    }

    public CompatibilityGroup getCompatibilityGroupByCode(String By) {
        return compatibilityGroupRepository.findByCode(By).orElseThrow(
                () -> new RecordNotFoundException("Compatibility group " + By + " not found")
        );
    }

    public SegregationRuleCode getSegregationRequirementCode(String code) {
        return segregationRuleCodeRepository.findByCode(code).orElseThrow(
                () -> new RecordNotFoundException("IMDG segregation requirement code " + code + " not found")
        );
    }

    public SegregationCode getSegregationCode(String code) {
        return segregationCodeRepository.findByCode(code).orElseThrow(
                () -> new RecordNotFoundException("Segregation code " + code + " not found")
        );
    }

    public SegregationGroupCode getSegregationGroupCode(String code) {
        return segregationGroupCodeRepository.findByCode(code).orElseThrow(
                () -> new RecordNotFoundException("Segregation group code " + code + " not found")
        );
    }

}