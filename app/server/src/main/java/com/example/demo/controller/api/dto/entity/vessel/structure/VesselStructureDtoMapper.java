package com.example.demo.controller.api.dto.entity.vessel.structure;

import com.example.demo.entity.vessel.structure.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VesselStructureDtoMapper {

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "vesselId", target = "vesselId")
    @Mapping(source = "bayIndex", target = "bayIndex")
    @Mapping(source = "vesselName", target = "vesselName")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "minShear", target = "minShear")
    @Mapping(source = "maxShear", target = "maxShear")
    @Mapping(source = "maxBending", target = "maxBending")
    @Mapping(source = "constWeight", target = "constWeight")
    @Mapping(source = "constWeightVcg", target = "constWeightVcg")
    @Mapping(source = "rows", target = "rowDtoList")
    @Mapping(source = "buoyancyPoints", target = "buoyancyPoints")
    BayDto toBayDto(Bay bay);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "rowIndex", target = "rowIndex")
    @Mapping(source = "lcg", target = "lcg")
    @Mapping(source = "tcg", target = "tcg")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "deck", target = "deckDto")
    @Mapping(source = "hold", target = "holdDto")
    RowDto toRowDto(Row row);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "tierIndex", target = "tierIndex")
    @Mapping(source = "allowReefer", target = "allowReefer")
    CellDto toCellDto(Cell cell);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "cells", target = "cellDtoList")
    DeckDto toCellDto(Deck deck);

    @Mapping(source = "contentHash", target = "contentHash")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "maxHeight", target = "maxHeight")
    @Mapping(source = "maxWeight20", target = "maxWeight20")
    @Mapping(source = "maxWeight40", target = "maxWeight40")
    @Mapping(source = "vcg", target = "vcg")
    @Mapping(source = "cells", target = "cellDtoList")
    HoldDto toCellDto(Hold hold);
}