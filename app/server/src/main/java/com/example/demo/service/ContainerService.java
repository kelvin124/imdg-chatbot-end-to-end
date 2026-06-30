package com.example.demo.service;

import com.example.demo.entity.container.Container;
import com.example.demo.repository.container.ContainerRepo;
import com.example.demo.service.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContainerService {

    @Autowired
    private final ContainerRepo containerRepo;

    public ContainerService(ContainerRepo containerRepo) {
        this.containerRepo = containerRepo;
    }

    public Container getContainer(String containerNo) {
        return containerRepo.findByContainerNo(containerNo)
                .orElseThrow(() -> new RecordNotFoundException("Container not found: " + containerNo));
    }

    public List<Container> getContainersRandomly(Integer count) {
        return containerRepo.getUnplannedContainersRandomly(count);
    }

    public Long countContainer() {
        return containerRepo.count();
    }

}
