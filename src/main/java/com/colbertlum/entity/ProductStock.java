package com.colbertlum.entity;

import java.util.List;

public class ProductStock {

    String id;
    Double stock;
    Double minKeepStock;

    public Double getAvailableStock(){
        if(stock > 0) return stock - (minKeepStock == null ? 0 : minKeepStock);
        else return 0d;
    }

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

}
