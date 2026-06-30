package com.example.demo.controller.api.dto.entity.vessel.profile;

import com.example.demo.entity.vessel.profile.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VesselProfileDtoMapper {

    @Mapping(target = "vesselId", source = "vesselId")
    @Mapping(target = "vesselName", source = "vesselName")
    @Mapping(target = "shipInfoDto", source = "shipInfo")
    @Mapping(target = "hydroPointDtoList", source = "hydroPoints")
    @Mapping(target = "tankDtoList", source = "tanks")
    VesselProfileDto toDto(VesselProfile vesselProfile);

    @Mapping(target = "bayCount", source = "bayCount")
    @Mapping(target = "rowCount", source = "rowCount")
    @Mapping(target = "tierCount", source = "tierCount")
    @Mapping(target = "tcgTolerance", source = "tcgTolerance")
    ShipInfoDto toDto(ShipInfoSnapshot shipInfoSnapshot);

    @Mapping(target = "displacement", source = "displacement")
    @Mapping(target = "minLcg", source = "minLcg")
    @Mapping(target = "maxLcg", source = "maxLcg")
    @Mapping(target = "metacenter", source = "metacenter")
    HydroPointDto toDto(HydroPoint hydroPoint);

    @Mapping(target = "capTon", source = "capTon")
    @Mapping(target = "tcg", source = "tcg")
    @Mapping(target = "vcgEmpty", source = "vcgEmpty")
    @Mapping(target = "vcgFull", source = "vcgFull")
    @Mapping(target = "bayCoverageDtoList", source = "bayCoverages")
    TankSnapshotDto toDto(Tank tank);

    @Mapping(target = "bayIndex", source = "bayIndex")
    @Mapping(target = "coverageRatio", source = "coverageRatio")
    BayCoverageDto toDto(BayCoverage bayCoverage);

}
