package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.SegregationCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SegregationCodeDtoMapper {

    @Mapping(target = "code", source = "code")
    @Mapping(target = "description", source = "description")
    SegregationCodeDto toDto(SegregationCode entity);

}