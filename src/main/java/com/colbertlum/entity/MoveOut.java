package com.colbertlum.entity;

public class MoveOut {
    private String sku;
    private String productName;
    private String variationName;
    private double quantity;
    private double price;
    private String parentSku;

    private Order order;
    private int foundRow;
    private String orderId;
    private String id;
    
    public String getOrderId() {
        if (orderId == null && order != null)
            return order.getId();
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParentSku() {
        return parentSku;
    }

    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }

    public MoveOut setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public double getProductSubTotal() {
        return quantity * price;
    }

    public Order getOrder() {
        return order;
    }

    public MoveOut setOrder(Order order) {
        this.order = order;
        return this;
    }

    public String getSku() {
        return sku;
    }

    public MoveOut setSku(String sku) {
        this.sku = sku;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public MoveOut setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getVariationName() {
        return variationName;
    }

    public MoveOut setVariationName(String variationName) {
        this.variationName = variationName;
        return this;
    }

    public double getQuantity() {
        return quantity;
    }

    public MoveOut setQuantity(double quantity) {
        this.quantity = quantity;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public MoveOut setPrice(double price) {
        this.price = price;
        return this;
    }

    public MoveOut setFoundRow(int foundRow) {
        this.foundRow = foundRow;
        return this;
    }

    public int getFoundRow() {
        return this.foundRow;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("MoveOut@")
            .append("'Order ID: " + getOrder().getId() + "', ")
            .append("'sku : " + getSku() + "', ")
            .append("'Name' : " + getProductName() + "', ")
            .append("'variation : " + getVariationName() + "', ")
            .append("'QTY : " + getQuantity() + "', ")
            .append("'Price : " + getPrice() + "', ")
            .append("'foundRow : " + getFoundRow() + "', ")
            .append("; /n")
            .toString();
    }

    
}
