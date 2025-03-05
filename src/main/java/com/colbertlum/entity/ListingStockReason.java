package com.colbertlum.entity;

public class ListingStockReason {

    private ListingStock info;
    private String status;

    public ListingStock getOnlineSalesInfo() {
        return info;
    }
    public ListingStockReason setOnlineSalesInfo(ListingStock info) {
        this.info = info;
        return this;
    }
    public String getStatus() {
        return status;
    }
    public ListingStockReason setStatus(String status) {
        this.status = status;
        return this;
    }

}
