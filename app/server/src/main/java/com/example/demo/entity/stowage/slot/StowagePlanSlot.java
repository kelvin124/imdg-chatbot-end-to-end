package com.example.demo.entity.stowage.slot;

import com.example.demo.repository.MongoDBCollection;
import com.example.demo.repository.MongoDocument;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = MongoDBCollection.STOWAGE_PLAN_SLOT)
public class StowagePlanSlot extends MongoDocument {

    @Field("contentHash")
    private String contentHash;

    @Field("planId")
    private String planId;

    @Field("position")
    private StowagePlanSlotPosition stowagePlanSlotPosition;

    @Field("containerSnapshot")
    private ContainerSnapshot containerSnapshot;

    @Field("allowReefer")
    private Boolean allowReefer;

    @Field("plannedSequence")
    private Integer plannedSequence;

    @Field("actualSequence")
    private Integer actualSequence;

    @Field("operationType")
    private String operationType;

    @Field("isRestow")
    private Boolean isRestow;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public StowagePlanSlotPosition getStowagePlanSlotPosition() {
        return stowagePlanSlotPosition;
    }

    public void setStowagePlanSlotPosition(StowagePlanSlotPosition stowagePlanSlotPosition) {
        this.stowagePlanSlotPosition = stowagePlanSlotPosition;
    }

    public ContainerSnapshot getContainerSnapshot() {
        return containerSnapshot;
    }

    public void setContainerSnapshot(ContainerSnapshot containerSnapshot) {
        this.containerSnapshot = containerSnapshot;
    }

    public Boolean getAllowReefer() {
        return allowReefer;
    }

    public void setAllowReefer(Boolean allowReefer) {
        this.allowReefer = allowReefer;
    }

    public Integer getPlannedSequence() {
        return plannedSequence;
    }

    public void setPlannedSequence(Integer plannedSequence) {
        this.plannedSequence = plannedSequence;
    }

    public Integer getActualSequence() {
        return actualSequence;
    }

    public void setActualSequence(Integer actualSequence) {
        this.actualSequence = actualSequence;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Boolean getIsRestow() {
        return isRestow;
    }

    public void setIsRestow(Boolean restow) {
        isRestow = restow;
    }

}
