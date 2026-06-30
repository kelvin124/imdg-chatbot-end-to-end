package com.example.demo.entity.imdg;

import com.example.demo.repository.MongoDBCollection;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = MongoDBCollection.SEGREGATION_RULE_CODE)
@CompoundIndex(name = "unique_idx", def = "{ 'code': 1 }", unique = true)
public class SegregationRuleCode {

    @Field("code")
    private String code;

    @Field("description")
    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
