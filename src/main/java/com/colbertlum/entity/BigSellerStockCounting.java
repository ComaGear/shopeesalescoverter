package com.colbertlum.entity;

public class BigSellerStockCounting {
    private String SKU; // SKU Name
    private String ProductName; // Title
    private String Warehouse;
    private String shelf;
    private String Area;
    private int onHand; // On Hand in Big Seller inventory for Merchant SKU 
    private int stock; // count
    private String note;
    private String imageUrl;

    public String getSKU() {
        return SKU;
    }
    public void setSKU(String sKU) {
        SKU = sKU;
    }
    public String getProductName() {
        return ProductName;
    }
    public void setProductName(String productName) {
        ProductName = productName;
    }
    public String getWarehouse() {
        return Warehouse;
    }
    public void setWarehouse(String warehouse) {
        Warehouse = warehouse;
    }
    public String getShelf() {
        return shelf;
    }
    public void setShelf(String shelf) {
        this.shelf = shelf;
    }
    public String getArea() {
        return Area;
    }
    public void setArea(String area) {
        Area = area;
    }
    public int getOnHand() {
        return onHand;
    }
    public void setOnHand(int onHand) {
        this.onHand = onHand;
    }
    public int getStock() {
        return stock;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    
}
