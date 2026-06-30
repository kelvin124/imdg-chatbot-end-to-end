package com.example.demo.controller.api.dto.entity.stowage;

import com.example.demo.entity.stowage.*;
import com.example.demo.entity.vessel.profile.ShipInfoSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StowagePlanDtoMapper {

    @Mapping(source = "planId", target = "planId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "vesselProfileSnapshot", target = "vesselProfileSnapshotDto")
    @Mapping(source = "baySnapshots", target = "baySnapshotDtoList")
    StowagePlanDto toDto(StowagePlan stowagePlan);

    @Mapping(source = "snapshotDate", target = "snapshotDate")
    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "vesselName", target = "vesselName")
    @Mapping(source = "shipInfoSnapshot", target = "shipInfoSnapshotDto")
    @Mapping(source = "hydroPointSnapshots", target = "hydroPointSnapshotDtoList")
    @Mapping(source = "tankSnapshots", target = "tankSnapshotDtoList")
    @Mapping(source = "vesselId", target = "vesselId")
    VesselProfileSnapshotDto toDto(VesselProfileSnapshot vesselProfileSnapshot);

    @Mapping(source = "snapshotDate", target = "snapshotDate")
    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "bayIndex", target = "bayIndex")
    @Mapping(source = "vesselName", target = "vesselName")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "minShear", target = "minShear")
    @Mapping(source = "maxShear", target = "maxShear")
    @Mapping(source = "maxBending", target = "maxBending")
    @Mapping(source = "constWeight", target = "constWeight")
    @Mapping(source = "constWeightVcg", target = "constWeightVcg")
    @Mapping(source = "buoyancyPoints", target = "buoyancyPoints")
    @Mapping(source = "rowSnapshots", target = "rowSnapshotDtoList")
    BaySnapshotDto toDto(BaySnapshot baySnapshot);

    @Mapping(source = "rowIndex", target = "rowIndex")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "tcg", target = "tcg")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "deckSnapshot", target = "deckSnapshotDtoList")
    @Mapping(source = "holdSnapshot", target = "holdSnapshotDtoList")
    RowSnapshotDto toDto(RowSnapshot rowSnapshot);

    @Mapping(source = "tierIndex", target = "tierIndex")
    @Mapping(source = "allowReefer", target = "allowReefer")
    CellSnapshotDto toDto(CellSnapshot cellSnapshot);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "cellSnapshots", target = "cellSnapshotDtoList")
    DeckSnapshotDto toDto(DeckSnapshot deck);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "cellSnapshots", target = "cellSnapshotDtoList")
    HoldSnapshotDto toDto(HoldSnapshot hold);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "capTon", target = "capTon")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "tcg", target = "tcg")
    @Mapping(source = "vcgEmpty", target = "vcgEmpty")
    @Mapping(source = "vcgFull", target = "vcgFull")
    @Mapping(source = "bayCoverageSnapshots", target = "bayCoverageDtoList")
    TankSnapshotDto toDto(TankSnapshot tankSnapshot);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "bayCount", target = "bayCount")
    @Mapping(source = "rowCount", target = "rowCount")
    @Mapping(source = "tierCount", target = "tierCount")
    @Mapping(source = "tcgTolerance", target = "tcgTolerance")
    @Mapping(source = "coverageRatio", target = "coverageRatio")
    ShipInfoSnapshotDto toDto(ShipInfoSnapshot shipInfoSnapshot);

    @Mapping(source = "displacement", target = "displacement")
    @Mapping(source = "minLcg", target = "minLcg")
    @Mapping(source = "maxLcg", target = "maxLcg")
    @Mapping(source = "metacenter", target = "metacenter")
    HydroPointSnapshotDto toDto(HydroPointSnapshot HydroPoint);

    @Mapping(source = "snapshotDate", target = "snapshotDate")
    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "bayIndex", target = "bayIndex")
    @Mapping(source = "coverageRatio", target = "coverageRatio")
    BayCoverageSnapshotDto toDto(BayCoverageSnapshot bayCoverageSnapshot);
}
