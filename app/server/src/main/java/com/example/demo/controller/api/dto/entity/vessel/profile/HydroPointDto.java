package com.example.demo.controller.api.dto.entity.vessel.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HydroPointDto {

    @JsonProperty("displacement")
    private Integer displacement;

    @JsonProperty("minLcg")
    private Double minLcg;

    @JsonProperty("maxLcg")
    private Double maxLcg;

    @JsonProperty("metacenter")
    private Double metacenter;

    public Integer getDisplacement() {
        return displacement;
    }

    public void setDisplacement(Integer displacement) {
        this.displacement = displacement;
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

    public Double getMinLcg() {
        return minLcg;
    }

    public void setMinLcg(Double minLcg) {
        this.minLcg = minLcg;
    }

}
