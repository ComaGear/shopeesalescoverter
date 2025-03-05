package com.colbertlum.entity;

public class BigSellerStockCounting extends ListingStock {

    private String Warehouse;
    private String shelf;
    private String Area;
    private int onHand; // On Hand in Big Seller inventory for Merchant SKU 
    private String note;
    private String imageUrl;

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
