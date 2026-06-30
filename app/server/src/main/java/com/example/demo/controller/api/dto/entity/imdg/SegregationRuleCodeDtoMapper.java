package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.SegregationRuleCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SegregationRuleCodeDtoMapper {

    @Mapping(target = "code", source = "code")
    @Mapping(target = "description", source = "description")
    SegregationRuleCodeDto toDto(SegregationRuleCode entity);

}