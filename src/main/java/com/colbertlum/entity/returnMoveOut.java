package com.colbertlum.entity;

public class ReturnMoveOut {
    private String sku;
    private String productName;
    private String variationName;
    private double quantity;
    private double price;
    private String orderId;
    private String returnStatus;
    private double statusQuantity;

    public static final String RETURNING = "returning";
    public static final String PARTICULAR_RECEIVED = "particular received";
    public static final String RECEIVED = "received";
    public static final String DAMAGED = "damaged";
    public static final String LOST = "lost";
    public static final String NONE = "none";

    // - retuning
    // - particular received
    // - received
    // - damaged
    // - lost
    // - none


    public String getReturnStatus() {
        return returnStatus;
    }
    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }
    public double getStatusQuantity() {
        return statusQuantity;
    }
    public void setStatusQuantity(double statusQuantity) {
        this.statusQuantity = statusQuantity;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getVariationName() {
        return variationName;
    }
    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public ReturnMoveOut(MoveOut moveOut){
        super();

        if(moveOut.getSku() != null){
            setSku(moveOut.getSku());
        } else {
            setSku(moveOut.getParentSku());
        }
        setProductName(moveOut.getProductName());
        setVariationName(moveOut.getVariationName());
        setQuantity(moveOut.getQuantity());
        setPrice(moveOut.getPrice());
        setOrderId(moveOut.getOrder().getId());
        setReturnStatus(RETURNING);
    }

    public ReturnMoveOut(){
        super();
    }

    public ReturnMoveOut clone(){
        ReturnMoveOut clone = new ReturnMoveOut();

        clone.setSku(getSku());
        clone.setProductName(getProductName());
        clone.setVariationName(getVariationName());
        clone.setQuantity(getQuantity());
        clone.setOrderId(getOrderId());
        clone.setPrice(getPrice());
        clone.setOrderId(getOrderId());
        clone.setReturnStatus(getReturnStatus());
        clone.setStatusQuantity(getStatusQuantity());

        return clone;
    }
}
