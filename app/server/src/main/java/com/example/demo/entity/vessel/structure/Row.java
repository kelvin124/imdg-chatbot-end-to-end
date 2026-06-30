package com.example.demo.entity.vessel.structure;

import org.springframework.data.mongodb.core.mapping.Field;

public class Row {

    @Field("contentHash")
    private String contentHash;

    @Field("rowIndex")
    private Integer rowIndex;

    @Field("tcg")
    private Double tcg;

    @Field("lcg")
    private Double lcg;

    @Field("identifier")
    private Integer identifier;

    @Field("maxHeight")
    private Double maxHeight;

    @Field("maxWeight20")
    private Double maxWeight20;

    @Field("maxWeight40")
    private Double maxWeight40;

    @Field("vcg")
    private Double vcg;

    @Field("deck")
    private Deck deck;

    @Field("hold")
    private Hold hold;

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public Double getTcg() {
        return tcg;
    }

    public void setTcg(Double tcg) {
        this.tcg = tcg;
    }

    public Double getLcg() {
        return lcg;
    }

    public void setLcg(Double lcg) {
        this.lcg = lcg;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public Double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Double getMaxWeight20() {
        return maxWeight20;
    }

    public void setMaxWeight20(Double maxWeight20) {
        this.maxWeight20 = maxWeight20;
    }

    public Double getMaxWeight40() {
        return maxWeight40;
    }

    public void setMaxWeight40(Double maxWeight40) {
        this.maxWeight40 = maxWeight40;
    }

    public Double getVcg() {
        return vcg;
    }

    public void setVcg(Double vcg) {
        this.vcg = vcg;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public Hold getHold() {
        return hold;
    }

    public void setHold(Hold hold) {
        this.hold = hold;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }
}
