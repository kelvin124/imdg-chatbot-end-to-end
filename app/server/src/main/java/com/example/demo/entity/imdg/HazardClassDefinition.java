package com.example.demo.entity.imdg;

import com.example.demo.repository.MongoDBCollection;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = MongoDBCollection.HAZARD_CLASS_DEFINITION)
@CompoundIndex(name = "unique_idx", def = "{ 'hazardClass': 1 }", unique = true)
public class HazardClassDefinition {

    @Field("hazardClass")
    private String hazardClass;

    @Field("substance")
    private String substance;

    public String getHazardClass() {
        return hazardClass;
    }

    public void setHazardClass(String hazardClass) {
        this.hazardClass = hazardClass;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

}
