package com.example.demo.repository.bay;

import com.example.demo.entity.vessel.structure.Bay;
import com.example.demo.entity.vessel.structure.Cell;
import com.example.demo.entity.vessel.structure.Row;
import com.example.demo.repository.MongoDBCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class BayCustomQueryImpl implements BayCustomQuery {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<Row> findRow(String vesselId, int bayIndex, int rowIndex) {
        MatchOperation match = match(Criteria.where("bayIndex").is(bayIndex)
                .and("vesselId").is(vesselId));

        ProjectionOperation project = project()
                .and(ArrayOperators.Filter.filter("rows")
                        .as("item")
                        .by(ComparisonOperators.valueOf("$$item.rowIndex")
                                .equalToValue(rowIndex)))
                .as("rows")
                .andExclude("_id");

        Aggregation aggregation = newAggregation(match, project);

        List<Bay> results = mongoTemplate.aggregate(
                aggregation, MongoDBCollection.VESSEL_STRUCTURE_BAY, Bay.class
        ).getMappedResults();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        Bay bay = results.getFirst();
        return Optional.of(bay.getRows().getFirst());
    }

    @Override
    public Optional<Cell> findCell(String vesselId, int bayIndex, int rowIndex, int tierIndex, String bayType) {
        MatchOperation match = match(Criteria.where("bayIndex").is(bayIndex)
                .and("vesselId").is(vesselId));

        ProjectionOperation rowProjectAndFilter = project()
                .and(ArrayOperators.Filter.filter("rows")
                        .as("item")
                        .by(ComparisonOperators.valueOf("$$item.rowIndex")
                                .equalToValue(rowIndex)))
                .as("rows")
                .andExclude("_id");

        ProjectionOperation cellProjectAndFilter = project()
                .and(ArrayOperators.Filter.filter("rows.cell")
                        .as("item")
                        .by(ComparisonOperators.valueOf("$$item.tierIndex")
                                .equalToValue(tierIndex)))
                .as("rows")
                .andExclude("_id");
        Aggregation aggregation = newAggregation(match, rowProjectAndFilter, cellProjectAndFilter);

        List<Bay> results = mongoTemplate.aggregate(
                aggregation, MongoDBCollection.VESSEL_STRUCTURE_BAY, Bay.class
        ).getMappedResults();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        Bay bay = results.getFirst();
        if (bay.getRows().isEmpty()) {
            return Optional.empty();
        }
        return Optional.empty();
    }
}
