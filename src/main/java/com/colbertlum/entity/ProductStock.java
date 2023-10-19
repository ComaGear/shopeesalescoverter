package com.colbertlum.entity;

public class ProductStock {

    String id;
    Double stock;
    Double minKeepStock;

    public Double getAvailableStock(){
        if(stock > 0) return stock - minKeepStock;
        else return 0d;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
