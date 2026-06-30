package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.SegregationCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegregationCodeRepository extends MongoRepository<SegregationCode, String> {

    @Query("{ 'code' : '?0' }")
    Optional<SegregationCode> findByCode(String code);

    @Query("{ 'code' : { $in: ?0 } }")
    List<SegregationCode> findByCodeIn(List<String> codes);
}