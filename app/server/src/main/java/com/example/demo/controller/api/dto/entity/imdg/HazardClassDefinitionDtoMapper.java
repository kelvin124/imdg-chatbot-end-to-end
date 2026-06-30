package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.HazardClassDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HazardClassDefinitionDtoMapper {

    @Mapping(target = "hazardClass", source = "hazardClass")
    @Mapping(target = "substance", source = "substance")
    HazardClassDefinitionDto toDto(HazardClassDefinition entity);
}

