package com.example.demo.repository.container;

import com.example.demo.entity.container.Container;

import java.util.List;

public interface ContainerCustomQuery {

    List<Container> getUnplannedContainersRandomly(Integer count);

}
