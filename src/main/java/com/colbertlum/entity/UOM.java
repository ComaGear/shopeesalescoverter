package com.colbertlum.entity;

import java.util.List;

public class UOM {
    private String productId;
    private String uom;
    private double rate;
    private String description;
    private double costPrice;

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "UOM [productId=" + productId + "]";
    }

    
    public static UOM binarySearch(String id, List<UOM> uoms){
        
        int lo = 0;
        int hi = uoms.size()-1;

        while(lo <= hi) {
            int mid = lo + (hi-lo) / 2;
            if(uoms.get(mid).getProductId().toLowerCase().compareTo(id.toLowerCase()) > 0) hi = mid-1; 
            else if(uoms.get(mid).getProductId().toLowerCase().compareTo(id.toLowerCase()) < 0) lo = mid+1;
            else{
                return uoms.get(mid);
            }
        }
        return null;
    }

}
