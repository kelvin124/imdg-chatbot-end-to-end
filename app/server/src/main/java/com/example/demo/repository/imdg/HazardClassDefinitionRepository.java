package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.HazardClassDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HazardClassDefinitionRepository extends MongoRepository<HazardClassDefinition, String> {

    @Query("{ 'hazardClass' : '?0' }")
    Optional<HazardClassDefinition> findByHazardClass(String hazardClass);

    @Query("{ 'hazardClass' : { $in: ?0 } }")
    List<HazardClassDefinition> findByHazardClassIn(List<String> hazardClasses);

}

