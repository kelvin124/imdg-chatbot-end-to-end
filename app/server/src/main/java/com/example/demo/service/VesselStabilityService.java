package com.example.demo.service;

import com.example.demo.config.VesselStabilityConfig;
import com.example.demo.entity.stowage.BaySnapshot;
import com.example.demo.entity.stowage.StowagePlan;
import com.example.demo.entity.stowage.slot.ContainerSnapshot;
import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import com.example.demo.service.domain.VesselStabilityCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VesselStabilityService {

    private final StowagePlanService stowagePlanService;
    private final VesselStabilityConfig vesselStabilityConfig;

    public VesselStabilityService(
            @Autowired StowagePlanService stowagePlanService,
            @Autowired VesselStabilityConfig vesselStabilityConfig
    ) {
        this.vesselStabilityConfig = vesselStabilityConfig;
        this.stowagePlanService = stowagePlanService;
    }

    //Check all at once
    public VesselStabilityCheckResult isStable(String planId) {
        VesselStabilityCheckResult result = new VesselStabilityCheckResult();

        StowagePlan stowagePlan = this.stowagePlanService.getStowagePlanById(planId);
        List<StowagePlanSlot> stowagePlanSlots = this.stowagePlanService.getStowagePlanSlotById(planId);
        Double cg = this.calculateCenterOfGravity(stowagePlan, stowagePlanSlots);
        Double kg = this.calculateVerticalCenterOfGravity(stowagePlan, stowagePlanSlots);

        result.setCg(cg);
        result.setKg(kg);
        result.setIsCgAcceptable(this.isCGAcceptable(cg, stowagePlan.getBaySnapshots()));
        result.setIsKgAcceptable(this.isVCGAcceptable(kg));
        result.setIsStable(result.getIsCgAcceptable() && result.getIsKgAcceptable());
        return result;
    }

    //KG (VCG)
    public Double calculateVerticalCenterOfGravity(String planId) {
        StowagePlan stowagePlan = this.stowagePlanService.getStowagePlanById(planId);
        List<StowagePlanSlot> stowagePlanSlots = this.stowagePlanService.getStowagePlanSlotById(planId);
        return this.calculateVerticalCenterOfGravity(stowagePlan, stowagePlanSlots);
    }

    private Double calculateVerticalCenterOfGravity(StowagePlan stowagePlan, List<StowagePlanSlot> stowagePlanSlots) {
        double totalWeight = calculateStowagePlanTotalWeight(stowagePlan, stowagePlanSlots);
        double totalBaysVerticalMoment = calculateTotalBaysVerticalMoment(stowagePlan);
        double totalContainerVerticalMoment = calculateTotalContainerVerticalMoment(stowagePlanSlots);
        double verticalMoment = totalBaysVerticalMoment + totalContainerVerticalMoment;
        return verticalMoment / totalWeight;
    }

    public VesselStabilityCheckResult isVCGAcceptable(String planId) {
        double kg = calculateVerticalCenterOfGravity(planId);
        Boolean isAcceptable = this.isVCGAcceptable(kg);
        VesselStabilityCheckResult result = new VesselStabilityCheckResult();
        result.setKg(kg);
        result.setIsKgAcceptable(isAcceptable);
        return result;
    }

    private Boolean isVCGAcceptable(Double kgValue) {
        return kgValue <= vesselStabilityConfig.getKg().getMax();
    }

    //CG
    public Double calculateCenterOfGravity(String planId) {
        StowagePlan stowagePlan = this.stowagePlanService.getStowagePlanById(planId);
        List<StowagePlanSlot> stowagePlanSlots = this.stowagePlanService.getStowagePlanSlotById(planId);
        return this.calculateCenterOfGravity(stowagePlan, stowagePlanSlots);
    }

    private Double calculateCenterOfGravity(StowagePlan stowagePlan, List<StowagePlanSlot> stowagePlanSlots) {
        double totalWeight = calculateStowagePlanTotalWeight(stowagePlan, stowagePlanSlots);
        double longitudinalMoment = calculateStowagePlanLongitudinalMoment(stowagePlan, stowagePlanSlots);
        return longitudinalMoment / totalWeight;
    }

    public VesselStabilityCheckResult isCGAcceptable(String planId) {
        StowagePlan stowagePlan = this.stowagePlanService.getStowagePlanById(planId);
        List<StowagePlanSlot> stowagePlanSlots = this.stowagePlanService.getStowagePlanSlotById(planId);
        Double cg = calculateCenterOfGravity(stowagePlan, stowagePlanSlots);
        Boolean isAcceptable = this.isCGAcceptable(cg, stowagePlan.getBaySnapshots());
        VesselStabilityCheckResult result = new VesselStabilityCheckResult();
        result.setCg(cg);
        result.setIsCgAcceptable(isAcceptable);
        return result;
    }

    public Boolean isCGAcceptable(Double cgValue, List<BaySnapshot> baySnapshots) {
        Double minCg = vesselStabilityConfig.getCg().getMin();
        Double maxCg = vesselStabilityConfig.getCg().getMax();
        Double vesselLength = this.getVesselLength(baySnapshots);
        Double cgRatio = cgValue / vesselLength;
        return minCg <= cgRatio && cgRatio <= maxCg;
    }

    private Double calculateStowagePlanTotalWeight(StowagePlan stowagePlan, List<StowagePlanSlot> stowagePlanSlots) {
        Double plannedContainerWeight = stowagePlanSlots.stream()
                .map(StowagePlanSlot::getContainerSnapshot)
                .mapToDouble(ContainerSnapshot::getWeight).sum();
        return plannedContainerWeight + stowagePlan.getLightWeight() +
                stowagePlan.getVesselProfileSnapshot().getTanksWeight(vesselStabilityConfig.getTank().getFullPercentage());
    }

    private Double calculateStowagePlanLongitudinalMoment(StowagePlan stowagePlan, List<StowagePlanSlot> stowagePlanSlots) {
        Map<Integer, Double> bayIndexToSlotMap = stowagePlanSlots.stream().collect(
                Collectors.groupingBy(slot ->
                                slot.getStowagePlanSlotPosition().getBayIndex(),
                        Collectors.summingDouble(slot -> slot.getContainerSnapshot().getWeight())
                )
        );
        return stowagePlan.getBaySnapshots().stream().filter(baySnapshot ->
                bayIndexToSlotMap.containsKey(baySnapshot.getBayIndex())
        ).mapToDouble(bay ->
                (bay.getConstWeight() + bayIndexToSlotMap.getOrDefault(bay.getBayIndex(), 0.0)) * bay.getLcg()
        ).sum();
    }

    private Double calculateTotalBaysVerticalMoment(StowagePlan stowagePlan) {
        return stowagePlan.getBaySnapshots().stream().mapToDouble(BaySnapshot::getVerticalMoment).sum();
    }

    private Double calculateTotalContainerVerticalMoment(List<StowagePlanSlot> stowagePlanSlots) {
        return stowagePlanSlots.stream().mapToDouble(slot ->
                slot.getContainerSnapshot().getWeight() * (
                        slot.getStowagePlanSlotPosition().getTierNumber() * vesselStabilityConfig.getKg().getDefaultTierHeight()
                )
        ).sum();
    }

    public Double getVesselLength(List<BaySnapshot> baySnapshots) {
        Double minLcg = baySnapshots.stream().mapToDouble(BaySnapshot::getLcg).min().orElse(0.0);
        Double maxLcg = baySnapshots.stream().mapToDouble(BaySnapshot::getLcg).max().orElse(0.0);
        return (Math.abs(minLcg) + maxLcg) * vesselStabilityConfig.getLcgScalingFactor();
    }

}