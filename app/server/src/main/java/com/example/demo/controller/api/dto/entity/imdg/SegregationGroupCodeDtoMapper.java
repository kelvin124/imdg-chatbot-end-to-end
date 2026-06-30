package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.SegregationGroupCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SegregationGroupCodeDtoMapper {

    @Mapping(target = "code", source = "code")
    @Mapping(target = "group", source = "group")
    @Mapping(target = "description", source = "description")
    SegregationGroupCodeDto toDto(SegregationGroupCode entity);

}