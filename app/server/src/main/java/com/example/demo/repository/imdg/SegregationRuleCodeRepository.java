package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.SegregationRuleCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegregationRuleCodeRepository extends MongoRepository<SegregationRuleCode, String> {

    @Query("{ 'code' : '?0' }")
    Optional<SegregationRuleCode> findByCode(String code);

    @Query("{ 'code' : { $in: ?0 } }")
    List<SegregationRuleCode> findByCodeIn(List<String> codes);
}