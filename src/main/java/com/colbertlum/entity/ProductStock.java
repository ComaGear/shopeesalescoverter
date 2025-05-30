package com.colbertlum.entity;

import java.util.List;

public class ProductStock {

    private String id;
    private double stock;
    private double minKeepStock;

    private String productName;
    private double allocatedStock;
    private double availableStock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setStock(double stock){
        this.stock = stock;
    }

    public static ProductStock binarySearch(String id, List<ProductStock> stockList) {
        if(id == null) return null;
 
        int mid = 0;
        int lo = 0;
        int hi = stockList.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(stockList.get(mid).getId().compareTo(id) > 0) hi = mid-1;
            else if(stockList.get(mid).getId().compareTo(id) < 0) lo = mid+1;
            else {
                return stockList.get(mid);
            }
        }
        return null;
    }

    public double getStock() {
        return stock;
    }

    public double getMinKeepStock() {
        return minKeepStock;
    }

    public void setMinKeepStock(double minKeepStock) {
        this.minKeepStock = minKeepStock;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getAllocatedStock() {
        return allocatedStock;
    }

    public void setAllocatedStock(double allocatedStock) {
        this.allocatedStock = allocatedStock;
    }

    public void setAvailableStock(double availableStock) {
        this.availableStock = availableStock;
    }

    public double getAvailableStock(){
        return availableStock;
    }
}
