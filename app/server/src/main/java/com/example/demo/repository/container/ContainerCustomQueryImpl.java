package com.example.demo.repository.container;

import com.example.demo.entity.container.Container;
import com.example.demo.repository.MongoDBCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ContainerCustomQueryImpl implements ContainerCustomQuery {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Container> getUnplannedContainersRandomly(Integer count) {
        SampleOperation sample = sample(count);
        LookupOperation lookup = lookup(
                MongoDBCollection.STOWAGE_PLAN_SLOT,
                "containerNo", "containerSnapshot.containerNo", "result"
        );
        MatchOperation match = match(
                Criteria.where("result").size(0)
        );
        ProjectionOperation project = project()
                .andExclude("result");
        Aggregation aggregation = newAggregation(sample, lookup, match, project);
        return mongoTemplate.aggregate(
                aggregation, MongoDBCollection.CONTAINER, Container.class
        ).getMappedResults();
    }
}
