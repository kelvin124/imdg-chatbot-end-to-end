package com.example.demo.entity.imdg;

import com.example.demo.repository.MongoDBCollection;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = MongoDBCollection.HAZARD_DIVISION_DEFINITION)
@CompoundIndex(name = "unique_idx", def = "{ 'hazardClass': 1, 'division': 1 }", unique = true)
public class HazardDivisionDefinition {

    @Field("hazardClass")
    private String hazardClass;

    @Field("division")
    private String division;

    @Field("substance")
    private String substance;

    public String getHazardClass() {
        return hazardClass;
    }

    public void setHazardClass(String hazardClass) {
        this.hazardClass = hazardClass;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSubstance() {
        return substance;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

}
