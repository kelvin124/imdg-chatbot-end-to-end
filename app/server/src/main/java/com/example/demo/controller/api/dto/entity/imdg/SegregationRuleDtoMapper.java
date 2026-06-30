package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.SegregationRule;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface SegregationRuleDtoMapper {

    @Mapping(target = "division", source = "division")
    @Mapping(target = "rules", source = "rules")
    SegregationRuleDto toDto(SegregationRule source, @Context Map<String, String> codeMap);

    default Map<String, String> mapRules(Map<String, String> rules, @Context Map<String, String> codeMap) {
        if (rules == null) {
            return null;
        }
        if (codeMap == null || codeMap.isEmpty()) {
            return rules;
        }

        Map<String, String> newRulesMap = new HashMap<>();
        rules.forEach((key, value) -> {
            var newKey = key.replace("_", ".");
            var newValue = codeMap.get(value);
            if (newValue != null) {
                newRulesMap.put(newKey, newValue);
            } else {
                newRulesMap.put(newKey, value);
            }
        });

        return newRulesMap;
    }


}