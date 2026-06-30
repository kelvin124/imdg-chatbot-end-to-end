package com.example.demo.repository.imdg;

import com.example.demo.entity.imdg.CompatibilityGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompatibilityGroupRepository extends MongoRepository<CompatibilityGroup, String> {

    @Query("{ 'code' : '?0' }")
    Optional<CompatibilityGroup> findByCode(String compatibilityGroup);

    @Query("{ 'code' : { $in: ?0 } }")
    List<CompatibilityGroup> findByCodeIn(List<String> compatibilityGroups);
}