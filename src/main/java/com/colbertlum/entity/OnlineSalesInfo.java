package com.colbertlum.entity;

public class OnlineSalesInfo {

    private String sku;
    private int quantity;
    private String parentSku;
    private double price;
    private String productId;
    private String variationId;
    private int foundRow;
    

    public int getFoundRow() {
        return foundRow;
    }

    public void setFoundRow(int foundRow) {
        this.foundRow = foundRow;
    }

    public String getParentSku() {
        return parentSku;
    }

    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVariationId() {
        return variationId;
    }

    public void setVariationId(String variationId) {
        this.variationId = variationId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSku() {
        return sku != null ? sku : parentSku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

}
