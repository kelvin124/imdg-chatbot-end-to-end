package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.CompatibilityGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompatibilityGroupDtoMapper {

    @Mapping(target = "code", source = "code")
    @Mapping(target = "allowedWith", source = "allowedWith")
    CompatibilityGroupDto toDto(CompatibilityGroup entity);

}