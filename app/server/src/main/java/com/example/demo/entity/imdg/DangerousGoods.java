package com.example.demo.entity.imdg;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = MongoDBCollection.DANGEROUS_GOODS)
@CompoundIndex(name = "unique_idx", def = "{ 'unNo': 1 }", unique = true)
public class DangerousGoods extends MongoDocument {

    @Field("unNo")
    private String unNo;

    @Field("psn")
    private String psn;

    @Field("dgClass")
    private String dgClass;

    @Field("division")
    private String division;

    @Field("compatibilityGroup")
    private String compatibilityGroup;

    @Field("subsidiaryHazard")
    private String subsidiaryHazard;

    @Field("segregation")
    private List<String> segregation;

    public String getDgClass() {
        return dgClass;
    }

    public void setDgClass(String dgClass) {
        this.dgClass = dgClass;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getPsn() {
        return psn;
    }

    public void setPsn(String psn) {
        this.psn = psn;
    }

    public List<String> getSegregation() {
        return segregation;
    }

    public void setSegregation(List<String> segregation) {
        this.segregation = segregation;
    }

    public String getSubsidiaryHazard() {
        return subsidiaryHazard;
    }

    public void setSubsidiaryHazard(String subsidiaryHazard) {
        this.subsidiaryHazard = subsidiaryHazard;
    }

    public String getUnNo() {
        return unNo;
    }

    public void setUnNo(String unNo) {
        this.unNo = unNo;
    }

    public String getCompatibilityGroup() {
        return compatibilityGroup;
    }

    public void setCompatibilityGroup(String compatibilityGroup) {
        this.compatibilityGroup = compatibilityGroup;
    }

}
