package com.example.demo.repository.vessel;

import com.example.demo.entity.vessel.profile.VesselProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface VesselProfileRepo extends MongoRepository<VesselProfile, String> {

    @Query("{'vesselId': ?0}")
    public Optional<VesselProfile> findByVesselId(String vesselId);

}
