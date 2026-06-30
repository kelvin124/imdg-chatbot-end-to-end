package com.example.demo.repository.stowage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class StowagePlanCustomQueryImpl implements StowagePlanCustomQuery {

    @Autowired
    private MongoTemplate mongoTemplate;


}
