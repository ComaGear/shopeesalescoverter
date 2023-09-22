package com.colbertlum.entity;

public class MoveOut {
    private String sku;
    private String productName;
    private String variationName;
    private double quantity;
    private double price;

    private Order order;
    private int foundRow;

    public double getProductSubTotal(){
        return quantity * price;
    }

    public Order getOrder() {
        return order;
    }
    public void setOrder(Order order) {
        this.order = order;
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

    public void setFoundRow(int foundRow) {
        this.foundRow = foundRow;
    }
    public int getFoundRow(int foundRow){
        return this.foundRow;
    }
}
