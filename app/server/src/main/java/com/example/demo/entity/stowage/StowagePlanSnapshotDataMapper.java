package com.example.demo.entity.stowage;

import com.example.demo.entity.container.Container;
import com.example.demo.entity.stowage.slot.ContainerSnapshot;
import com.example.demo.entity.vessel.profile.BayCoverage;
import com.example.demo.entity.vessel.profile.HydroPoint;
import com.example.demo.entity.vessel.profile.Tank;
import com.example.demo.entity.vessel.profile.VesselProfile;
import com.example.demo.entity.vessel.structure.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StowagePlanSnapshotDataMapper {

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "vesselName", target = "vesselName")
    @Mapping(source = "bayIndex", target = "bayIndex")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "minShear", target = "minShear")
    @Mapping(source = "maxShear", target = "maxShear")
    @Mapping(source = "maxBending", target = "maxBending")
    @Mapping(source = "constWeight", target = "constWeight")
    @Mapping(source = "constWeightVcg", target = "constWeightVcg")
    @Mapping(source = "buoyancyPoints", target = "buoyancyPoints")
    @Mapping(source = "rows", target = "rowSnapshots")
    BaySnapshot toSnapshot(Bay bay);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "rowIndex", target = "rowIndex")
    @Mapping(source = "deck", target = "deckSnapshot")
    @Mapping(source = "hold", target = "holdSnapshot")
    RowSnapshot toSnapshot(Row row);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "cells", target = "cellSnapshots")
    DeckSnapshot toSnapshot(Deck deck);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "cells", target = "cellSnapshots")
    HoldSnapshot toSnapshot(Hold hold);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "tierIndex", target = "tierIndex")
    @Mapping(source = "allowReefer", target = "allowReefer")
    CellSnapshot toSnapshot(Cell cell);

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
    ContainerSnapshot toSnapshot(Container container);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "vesselName", target = "vesselName")
    @Mapping(source = "vesselId", target = "vesselId")
    @Mapping(source = "shipInfo", target = "shipInfoSnapshot")
    @Mapping(source = "hydroPoints", target = "hydroPointSnapshots")
    @Mapping(source = "tanks", target = "tankSnapshots")
    VesselProfileSnapshot toSnapshot(VesselProfile vesselProfile);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "capTon", target = "capTon")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "tcg", target = "tcg")
    @Mapping(source = "vcgEmpty", target = "vcgEmpty")
    @Mapping(source = "vcgFull", target = "vcgFull")
    @Mapping(source = "bayCoverages", target = "bayCoverageSnapshots")
    TankSnapshot toSnapshot(Tank tank);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "bayIndex", target = "bayIndex")
    @Mapping(source = "coverageRatio", target = "coverageRatio")
    BayCoverageSnapshot toSnapshot(BayCoverage bayCoverage);

    @Mapping(source = "displacement", target = "displacement")
    @Mapping(source = "minLcg", target = "minLcg")
    @Mapping(source = "maxLcg", target = "maxLcg")
    @Mapping(source = "metacenter", target = "metacenter")
    HydroPointSnapshot toSnapshot(HydroPoint HydroPoint);
}
