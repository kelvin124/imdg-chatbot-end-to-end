package com.example.demo.entity.stowage;

import org.springframework.data.mongodb.core.mapping.Field;

public class HydroPointSnapshot {

    @Field("displacement")
    private Integer displacement;

    @Field("minLcg")
    private Double minLcg;

    @Field("maxLcg")
    private Double maxLcg;

    @Field("metacenter")
    private Double metacenter;

    public Integer getDisplacement() {
        return displacement;
    }

    public void setDisplacement(Integer displacement) {
        this.displacement = displacement;
    }

    public Double getMinLcg() {
        return minLcg;
    }

    public void setMinLcg(Double minLcg) {
        this.minLcg = minLcg;
    }

    public Double getMaxLcg() {
        return maxLcg;
    }

    public void setMaxLcg(Double maxLcg) {
        this.maxLcg = maxLcg;
    }

    public Double getMetacenter() {
        return metacenter;
    }

    public void setMetacenter(Double metacenter) {
        this.metacenter = metacenter;
    }

}
