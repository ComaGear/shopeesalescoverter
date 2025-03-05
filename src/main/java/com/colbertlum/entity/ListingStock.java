package com.colbertlum.entity;

public class ListingStock {
    private String name;
    private String sku;
    private String productId;
    private int stock;

    public String getName() {
        return name;
    }
    public void setName(String productName) {
        this.name = productName;
    }
    public String getSku() {
        return sku;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }

    
}
