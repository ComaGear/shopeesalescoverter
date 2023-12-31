package com.colbertlum.entity;

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

}
