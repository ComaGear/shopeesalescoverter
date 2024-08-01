package com.colbertlum.entity;

public class Meas {
    
    private String id;
    private String relativeId;
    private double measurement;
    private String updateRule;
    private String name;
    private String onlineProductName;
    private String onlineVariationName;
    
    public String getOnlineProductName() {
        return onlineProductName;
    }
    public void setOnlineProductName(String onlineProductName) {
        this.onlineProductName = onlineProductName;
    }
    public String getOnlineVariationName() {
        return onlineVariationName;
    }
    public void setOnlineVariationName(String onlineVariationName) {
        this.onlineVariationName = onlineVariationName;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRelativeId() {
        return relativeId;
    }
    public void setRelativeId(String relativeId) {
        this.relativeId = relativeId;
    }
    public double getMeasurement() {
        return measurement;
    }
    public void setMeasurement(double measurement) {
        this.measurement = measurement;
    }
    public String getUpdateRule() {
        return updateRule;
    }
    public void setUpdateRule(String updateRule) {
        this.updateRule = updateRule;
    }
}
