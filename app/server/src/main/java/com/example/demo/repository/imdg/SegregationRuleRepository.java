package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.SegregationRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegregationRuleRepository extends MongoRepository<SegregationRule, String> {

    @Query("{ 'division' : ?0 }")
    Optional<SegregationRule> findByDivision(String division);

    @Query("{ 'division' : { $in: ?0 } }")
    List<SegregationRule> findByDivisionIn(List<String> divisions);
}