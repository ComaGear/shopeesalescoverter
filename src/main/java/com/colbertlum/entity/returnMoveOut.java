package com.colbertlum.entity;

public class returnMoveOut {
    private String sku;
    private String productName;
    private String variationName;
    private double quantity;
    private double price;
    private String orderId;
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

    public returnMoveOut(MoveOut moveOut){
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
    }

    public returnMoveOut(){
        super();
    }
}
