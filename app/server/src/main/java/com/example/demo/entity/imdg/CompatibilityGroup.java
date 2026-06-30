package com.example.demo.entity.imdg;

import com.example.demo.repository.MongoDBCollection;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = MongoDBCollection.COMPATIBILITY_GROUP)
@CompoundIndex(name = "unique_idx", def = "{ 'code': 1 }", unique = true)
public class CompatibilityGroup {

    @Field("code")
    private String code;

    @Field("allowedWith")
    private List<String> allowedWith;

    public List<String> getAllowedWith() {
        return allowedWith;
    }

    public void setAllowedWith(List<String> allowedWith) {
        this.allowedWith = allowedWith;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
