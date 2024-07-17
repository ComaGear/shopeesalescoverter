package com.colbertlum.entity;

public class ItemMovementStatus {

    private String sku;
    private String parentSku;
    private double quantity;
    private String orderId;
    private String trackingNumber;
    
    private String status;
    private double receivedQuantity;
    
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getParentSku() {
        return parentSku;
    }
    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getReceivedQuantity() {
        return receivedQuantity;
    }
    public void setReceivedQuantity(double receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }
}
