package com.example.demo.controller.api;

import com.example.demo.controller.api.dto.entity.container.ContainerDto;
import com.example.demo.controller.api.dto.entity.container.ContainerDtoMapper;
import com.example.demo.controller.api.request.container.CheckIfContainerIsPlannedRequest;
import com.example.demo.entity.container.Container;
import com.example.demo.service.ContainerService;
import com.example.demo.service.StowagePlanSlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/container")
public class ContainerControllerV1 {

    private final ContainerService containerService;
    private final ContainerDtoMapper containerDtoMapper;
    private final StowagePlanSlotService stowagePlanSlotService;

    public ContainerControllerV1(
            @Autowired ContainerService containerService,
            @Autowired ContainerDtoMapper containerDtoMapper,
            @Autowired StowagePlanSlotService stowagePlanSlotService
    ) {
        this.containerService = containerService;
        this.containerDtoMapper = containerDtoMapper;
        this.stowagePlanSlotService = stowagePlanSlotService;
    }

    @GetMapping("/pending-count")
    public ResponseEntity<Long> getPendingContainers() {
        var containerCount = containerService.countContainer();
        return ResponseEntity.ok().body(containerCount);
    }

    @PostMapping("/check-planned")
    public ResponseEntity<Boolean> checkIfContainerIsPlanned(
            @Valid @RequestBody CheckIfContainerIsPlannedRequest request
    ) {
        var isPlanned = stowagePlanSlotService.isContainerPlanned(
                request.containerNo, request.operationType, request.stowagePlanId
        );
        return ResponseEntity.ok(isPlanned);
    }

    @GetMapping("/random")
    public ResponseEntity<List<ContainerDto>> getContainers(@RequestParam("count") int count) {
        List<Container> containers = containerService.getContainersRandomly(count);
        return ResponseEntity.ok().body(
                containers.stream().map(containerDtoMapper::toDto).toList()
        );
    }

    @GetMapping("/{containerNo}")
    public ResponseEntity<ContainerDto> getContainerByNo(@PathVariable String containerNo) {
        Container container = containerService.getContainer(containerNo);
        return ResponseEntity.ok().body(containerDtoMapper.toDto(container));
    }


}
