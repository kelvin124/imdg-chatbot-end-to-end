package com.example.demo.repository.bay;

import com.example.demo.entity.vessel.structure.Bay;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BayRepo extends MongoRepository<Bay, String>,

        BayCustomQuery {

    @Query("{ 'vesselId': ?0 }")
    List<Bay> findByVesselId(String vesselId);

    @Query("{ 'vesselId': ?0, 'bayIndex': ?1 }")
    Optional<Bay> findBay(String vesselId, int bayIndex);

}
