package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.HazardDivisionDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HazardDivisionDefinitionRepository extends MongoRepository<HazardDivisionDefinition, String> {

    @Query("{ 'division' : '?0' }")
    List<HazardDivisionDefinition> findByDivision(String division);

    @Query("{ 'division' :  { $in: ?0 } }")
    List<HazardDivisionDefinition> findByDivision(List<String> division);

    @Query("{ 'hazardClass' : '?0' }")
    List<HazardDivisionDefinition> findByHazardClass(String hazardClass);

    @Query("{ 'hazardClass' : { $in: ?0 } }")
    List<HazardDivisionDefinition> findByHazardClassIn(List<String> hazardClasses);

}

