package com.example.demo.entity.imdg;

import com.example.demo.repository.MongoDBCollection;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = MongoDBCollection.SEGREGATION_RULE)
@CompoundIndex(name = "unique_idx", def = "{ 'division': 1 }", unique = true)
public class SegregationRule {

    @Field("division")
    private String division;

    @Field("rules")
    private Map<String, String> rules;

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public void setRules(Map<String, String> rules) {
        this.rules = rules;
    }

}
