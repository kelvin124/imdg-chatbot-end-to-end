package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.HazardDivisionDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HazardDivisionDefinitionDtoMapper {

    @Mapping(target = "hazardClass", source = "hazardClass")
    @Mapping(target = "division", source = "division")
    @Mapping(target = "substance", source = "substance")
    HazardDivisionDefinitionDto toDto(HazardDivisionDefinition entity);
}

