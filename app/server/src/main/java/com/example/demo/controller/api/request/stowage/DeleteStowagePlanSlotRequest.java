package com.example.demo.controller.api.request.stowage;

import com.example.demo.service.domain.OperationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeleteStowagePlanSlotRequest {

    @JsonProperty("stowagePlanId")
    @NotBlank(message = "Stowage Plan ID must not be empty")
    private String stowagePlanId;

    @JsonProperty("bayIndex")
    @NotNull(message = "bayIndex must not be null")
    @PositiveOrZero(message = "bayIndex must >= 0")
    private Integer bayIndex;

    @JsonProperty("rowIndex")
    @NotNull(message = "rowIndex must not be null")
    @PositiveOrZero(message = "rowIndex must >= 0")
    private Integer rowIndex;

    @JsonProperty("tierIndex")
    @NotNull(message = "tierIndex must not be null")
    @PositiveOrZero(message = "tierIndex must >= 0")
    private Integer tierIndex;

    @JsonProperty("operationType")
    @NotBlank(message = "must provide operationType")
    @Pattern(regexp = OperationType.REGEX_PATTERN,
            message = "operationType must be DS for discharge, LD for loading, SH for shifting")
    private String operationType;

    public String getStowagePlanId() {
        return stowagePlanId;
    }

    public void setStowagePlanId(String stowagePlanId) {
        this.stowagePlanId = stowagePlanId;
    }

    public Integer getBayIndex() {
        return bayIndex;
    }

    public void setBayIndex(Integer bayIndex) {
        this.bayIndex = bayIndex;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Integer getTierIndex() {
        return tierIndex;
    }

    public void setTierIndex(Integer tierIndex) {
        this.tierIndex = tierIndex;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

}