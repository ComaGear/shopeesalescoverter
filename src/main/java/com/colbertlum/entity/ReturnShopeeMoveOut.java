package com.colbertlum.entity;

public class ReturnShopeeMoveOut extends ReturnMoveOut{

    private String productName;
    private String variationName;
    private String parentSku;

    private double productSubtotal;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public String getParentSku() {
        return parentSku;
    }

    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }

    public double getProductSubtotal() {
        return productSubtotal;
    }

    public void setProductSubtotal(double productSubtotal) {
        this.productSubtotal = productSubtotal;
    }

    
}