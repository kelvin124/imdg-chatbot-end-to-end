package com.example.demo.controller.api;

import com.example.demo.controller.api.request.stability.*;
import com.example.demo.controller.api.response.stability.VesselStabilityCheckResponse;
import com.example.demo.service.VesselStabilityService;
import com.example.demo.service.domain.VesselStabilityCheckResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/stability", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
public class StabilityControllerV1 {

    private final VesselStabilityService vesselStabilityService;

    public StabilityControllerV1(
            @Autowired VesselStabilityService vesselStabilityService
    ) {
        this.vesselStabilityService = vesselStabilityService;
    }

    @PostMapping(path = "/cg/compute")
    public ResponseEntity<VesselStabilityCheckResponse> computeCgApiV1(@RequestBody ComputeCgRequest request) {
        double cg = vesselStabilityService.calculateCenterOfGravity(request.getStowagePlanId());
        VesselStabilityCheckResponse response = new VesselStabilityCheckResponse();
        response.setCg(cg);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/kg/compute")
    public ResponseEntity<VesselStabilityCheckResponse> computeKgApiV1(@RequestBody ComputeKgRequest request) {
        double kg = vesselStabilityService.calculateVerticalCenterOfGravity(request.getStowagePlanId());
        VesselStabilityCheckResponse response = new VesselStabilityCheckResponse();
        response.setKg(kg);
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/kg/validate")
    public ResponseEntity<VesselStabilityCheckResponse> validateKgApiV1(@RequestBody ValidateKgRequest request) {
        VesselStabilityCheckResult result = vesselStabilityService.isVCGAcceptable(request.getStowagePlanId());
        VesselStabilityCheckResponse response = new VesselStabilityCheckResponse();
        response.setKg(result.getKg());
        response.setIsKgAcceptable(result.getIsKgAcceptable());
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/cg/validate")
    public ResponseEntity<VesselStabilityCheckResponse> validateCgApiV1(@RequestBody ValidateCgRequest request) {
        VesselStabilityCheckResponse response = new VesselStabilityCheckResponse();
        VesselStabilityCheckResult result = vesselStabilityService.isCGAcceptable(request.getStowagePlanId());
        response.setCg(result.getCg());
        response.setIsCgAcceptable(result.getIsCgAcceptable());
        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/vessel/validate")
    public ResponseEntity<VesselStabilityCheckResponse> isVesselStable(@RequestBody CheckVesselStabilityRequest request) {
        VesselStabilityCheckResponse response = new VesselStabilityCheckResponse();
        VesselStabilityCheckResult result = vesselStabilityService.isStable(request.getStowagePlanId());
        response.setIsStable(result.getIsStable());
        response.setCg(result.getCg());
        response.setKg(result.getKg());
        response.setIsCgAcceptable(result.getIsCgAcceptable());
        response.setIsKgAcceptable(result.getIsKgAcceptable());
        return ResponseEntity.ok(response);
    }

}