package com.example.demo.repository.stowage;

import com.example.demo.entity.stowage.slot.StowagePlanSlot;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StowagePlanSlotRepo extends MongoRepository<StowagePlanSlot, String> {

    @Query("""
            {
                'planId': ?0
            }
            """)
    List<StowagePlanSlot> findAllPlannedSlots(String planId);

    @DeleteQuery("""
            {
                'planId': ?0
            }
            """)
    void deleteAllByPlanId(String planId);

    @Query("""
            {
                'position.bayIndex': ?0,
                'operationType': '?1',
                'planId': ?2
            }
            """)
    List<StowagePlanSlot> findByBayIndex(
            int bayIndex, String operationType, String planId
    );

    @Query("""
            {
                'position.bayIndex': ?0,
                'position.rowIndex': ?1,
                'operationType': '?2',
                'planId': ?3
            }
            """)
    List<StowagePlanSlot> findByBayRowIndex(
            int bayIndex, int rowIndex, String operationType, String planId
    );

    @Query("""
            {
                'position.bayIndex': ?0,
                'position.rowIndex': ?1,
                'position.tierIndex': ?2,
                'operationType': '?3',
                'planId': ?4
            }
            """)
    Optional<StowagePlanSlot> findByBayRowTierIndex(
            int bayIndex, int rowIndex, int tierIndex, String operationType,
            String planId
    );

    @Query(value = """
            {
                'position.bayIndex': ?0,
                'position.rowIndex': ?1,
                'position.tierIndex': ?2,
                'operationType': '?3',
                'containerSnapshot.containerNo': { $ne: null },
                'planId': ?4
            }
            """, exists = true)
    Boolean isStowagePlanSlotOccupied(
            int bayIndex, int rowIndex, int tierIndex, String operationType, String planId
    );

    @Query(value = """
            {
                'containerSnapshot.containerNo': '?0',
                'operationType': '?1',
                'planId': ?2
            }
            """)
    Optional<StowagePlanSlot> getSlotByContainerNoAndOpType(String containerNo, String operationType, String planId);

    @DeleteQuery(value = """
            {
                'position.bayIndex': ?0,
                'position.rowIndex': ?1,
                'position.tierIndex': ?2,
                'operationType': '?3',
                'containerSnapshot.containerNo': '?4',
                'planId': ?5
            }
            """)
    void deleteSlot(
            int bayIndex, int rowIndex, int tierIndex,
            String containerNo, String operationType, String planId
    );

}
