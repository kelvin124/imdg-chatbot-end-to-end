package com.example.demo.controller.api.dto.entity.container;

import com.example.demo.entity.container.Container;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContainerDtoMapper {

    @Mapping(target = "containerNo", source = "containerNo")
    @Mapping(target = "weight", source = "weight")
    @Mapping(target = "height", source = "height")
    @Mapping(target = "length", source = "length")
    @Mapping(target = "portOfLoading", source = "portOfLoading")
    @Mapping(target = "portOfDischarge", source = "portOfDischarge")
    @Mapping(target = "isDg", source = "isDg")
    @Mapping(target = "imdgClass", source = "imdgClass")
    @Mapping(target = "undgCode", source = "undgCode")
    @Mapping(target = "isOutOfGauge", source = "isOutOfGauge")
    @Mapping(target = "isHighCube", source = "isHighCube")
    @Mapping(target = "cargo", source = "cargo")
    @Mapping(target = "isReefer", source = "isReefer")
    ContainerDto toDto(Container container);

}
