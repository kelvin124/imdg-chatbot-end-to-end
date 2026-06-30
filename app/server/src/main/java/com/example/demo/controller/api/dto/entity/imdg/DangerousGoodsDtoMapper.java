package com.example.demo.controller.api.dto.entity.imdg;

import com.example.demo.entity.imdg.DangerousGoods;
import com.example.demo.entity.imdg.HazardClassDefinition;
import com.example.demo.entity.imdg.HazardDivisionDefinition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DangerousGoodsDtoMapper {

    @Mapping(target = "unNo", source = "unNo")
    @Mapping(target = "psn", source = "psn")
    @Mapping(target = "dgClass", source = "dgClass")
    @Mapping(target = "division", source = "division")
    @Mapping(target = "compatibilityGroup", source = "compatibilityGroup")
    @Mapping(target = "subsidiaryHazard", source = "subsidiaryHazard")
    @Mapping(target = "segregation", source = "segregation")
    DangerousGoodsDto toDto(DangerousGoods dangerousGoods);

    default DangerousGoodsFullDto toDto(
            DangerousGoods dangerousGoods, List<SegregationCodeDto> segregationCodes,
            List<SegregationGroupCodeDto> segregationGroupCodes, HazardClassDefinition classDefinition,
            HazardDivisionDefinition divisionDefinition
    ) {
        DangerousGoodsFullDto dto = new DangerousGoodsFullDto();
        dto.setUnNo(dangerousGoods.getUnNo());
        dto.setPsn(dangerousGoods.getPsn());
        dto.setDgClass(dangerousGoods.getDgClass());
        dto.setDivision(dangerousGoods.getDivision());
        dto.setSubsidiaryHazard(dangerousGoods.getSubsidiaryHazard());

        var sgCodeMap = segregationCodes.stream().collect(
                Collectors.toMap(SegregationCodeDto::getCode, Function.identity())
        );

        var sggCodeMap = segregationGroupCodes.stream().collect(
                Collectors.toMap(SegregationGroupCodeDto::getCode, Function.identity())
        );

        if (dangerousGoods.getSegregation() != null && !dangerousGoods.getSegregation().isEmpty()) {
            var sgdtoList = dangerousGoods.getSegregation().stream().map(sgCodeMap::get).toList();
            var sggDtoList = dangerousGoods.getSegregation().stream().map(sggCodeMap::get).toList();
            dto.setSegregationCodeDto(sgdtoList);
            dto.setSegregationGroupCodeDto(sggDtoList);
        } else {
            dto.setSegregationCodeDto(List.of());
            dto.setSegregationGroupCodeDto(List.of());
        }
        dto.setClassSubstance(classDefinition != null ? classDefinition.getSubstance() : null);
        dto.setDivisionSubstance(divisionDefinition != null ? divisionDefinition.getSubstance() : null);
        return dto;

    }

}
