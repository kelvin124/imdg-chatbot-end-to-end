package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.SegregationGroupCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegregationGroupCodeRepository extends MongoRepository<SegregationGroupCode, String> {

    @Query("{ 'code' : '?0' }")
    Optional<SegregationGroupCode> findByCode(String code);

    @Query("{ 'code' : { $in: ?0 } }")
    List<SegregationGroupCode> findByCodeIn(List<String> codes);
}