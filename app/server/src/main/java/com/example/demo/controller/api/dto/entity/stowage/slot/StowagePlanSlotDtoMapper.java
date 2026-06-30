package com.example.demo.controller.api.dto.entity.stowage.slot;

import com.example.demo.entity.stowage.slot.ContainerSnapshot;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.entity.stowage.slot.StowagePlanSlotPosition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StowagePlanSlotDtoMapper {

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "planId", target = "planId")
    @Mapping(source = "stowagePlanSlotPosition", target = "stowagePlanSlotPositionDto")
    @Mapping(source = "containerSnapshot", target = "containerSnapshotDto")
    @Mapping(source = "allowReefer", target = "allowReefer")
    @Mapping(source = "plannedSequence", target = "plannedSequence")
    @Mapping(source = "actualSequence", target = "actualSequence")
    @Mapping(source = "operationType", target = "operationType")
    @Mapping(source = "isRestow", target = "isRestow")
    StowagePlanSlotDto toDto(StowagePlanSlot stowagePlanSlot);

    @Mapping(source = "bayIndex", target = "bayIndex")
    @Mapping(source = "rowIndex", target = "rowIndex")
    @Mapping(source = "tierIndex", target = "tierIndex")
    StowagePlanSlotPositionDto toDto(StowagePlanSlotPosition stowagePlanSlotPosition);

    @Mapping(source = "snapshotDate", target = "snapshotDate")
    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "containerNo", target = "containerNo")
    @Mapping(source = "weight", target = "weight")
    @Mapping(source = "height", target = "height")
    @Mapping(source = "length", target = "length")
    @Mapping(source = "portOfLoading", target = "portOfLoading")
    @Mapping(source = "portOfDischarge", target = "portOfDischarge")
    @Mapping(source = "isDg", target = "isDg")
    @Mapping(source = "imdgClass", target = "imdgClass")
    @Mapping(source = "undgCode", target = "undgCode")
    @Mapping(source = "isOutOfGauge", target = "isOutOfGauge")
    @Mapping(source = "isHighCube", target = "isHighCube")
    @Mapping(source = "cargo", target = "cargo")
    @Mapping(source = "isReefer", target = "isReefer")
    ContainerSnapshotDto toDto(ContainerSnapshot containerSnapshot);

}
