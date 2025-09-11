package com.colbertlum.entity;

import com.colbertlum.Imputer.Utils.MoveOutFactory;

public class ReturnMoveOut {
    private String sku;
    private String name;
    private double quantity;
    private double price;
    private String orderId;
    private String returnStatus;
    private double statusQuantity;
    private String productId;
    private ReturnOrder returnOrder;

    private boolean settled;
    private double returnedQuantity;
    
    public double getReturnedQuantity() {
        return returnedQuantity;
    }
    public void setReturnedQuantity(double returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }
    public boolean isSettled() {
        return settled;
    }
    public void setSettled(boolean settled) {
        this.settled = settled;
    }
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
    
    
    public String getProductId() {
        return productId;
    }
    public void setProductId(String id) {
        this.productId = id;
    }
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

        setSku(moveOut.getSku());
        setName(moveOut.getName());
        setQuantity(moveOut.getQuantity());
        setPrice(moveOut.getPrice());
        setOrderId(moveOut.getOrder().getId());
        setReturnStatus(RETURNING);
        setProductId(moveOut.getProductId());
        setReturnedQuantity(MoveOutFactory.getReturnedQuantity(moveOut));
    }

    public ReturnMoveOut(){
        super();
    }

    public ReturnMoveOut clone(){
        ReturnMoveOut clone = new ReturnMoveOut();

        return copy(clone);
    }

    public ReturnMoveOut copy(ReturnMoveOut moveOut){

        moveOut.setSku(getSku());
        moveOut.setName(getName());
        moveOut.setQuantity(getQuantity());
        moveOut.setOrderId(getOrderId());
        moveOut.setPrice(getPrice());
        moveOut.setOrderId(getOrderId());
        moveOut.setReturnStatus(getReturnStatus());
        moveOut.setStatusQuantity(getStatusQuantity());
        moveOut.setProductId(getProductId());
        moveOut.setReturnOrder(getReturnOrder());
        moveOut.setReturnedQuantity(getReturnedQuantity());

        return moveOut;
    }
    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }
    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
}
