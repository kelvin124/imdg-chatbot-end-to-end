package com.example.demo.repository.container;

import com.example.demo.entity.container.Container;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ContainerRepo extends MongoRepository<Container, String>, ContainerCustomQuery {

    @Query("{ 'containerNo': ?0 }")
    Optional<Container> findByContainerNo(String containerNo);

}
