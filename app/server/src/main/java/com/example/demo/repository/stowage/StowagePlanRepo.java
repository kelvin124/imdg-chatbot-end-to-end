package com.example.demo.repository.stowage;

import com.example.demo.entity.stowage.StowagePlan;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface StowagePlanRepo extends MongoRepository<StowagePlan, String>, StowagePlanCustomQuery, StowagePlanCustomCmd {

    @Query(value = "{ 'planId': ?0 }")
    Optional<StowagePlan> findByPlanId(String planId);

    @DeleteQuery(value = "{ 'planId': ?0 }")
    void deleteByPlanId(String planId);
    
}
