package com.colbertlum.entity;

public class ReturnShopeeMoveOut extends ReturnMoveOut{

    private String productName;
    private String variationName;
    private String parentSku;

    private double productSubtotal;
    private double platformDiscount;
    

    public double getPlatformDiscount() {
        return platformDiscount;
    }

    public void setPlatformDiscount(double platformDiscount) {
        this.platformDiscount = platformDiscount;
    }

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

    @Override
    public ReturnMoveOut clone() {
        ReturnShopeeMoveOut returnShopeeMoveOut = new ReturnShopeeMoveOut();
        
        copy(returnShopeeMoveOut);

        return returnShopeeMoveOut;
    }

    @Override
    public ReturnMoveOut copy(ReturnMoveOut moveOut) {
        super.copy(moveOut);

        if(moveOut instanceof ReturnShopeeMoveOut) {
            ReturnShopeeMoveOut rMoveOut = (ReturnShopeeMoveOut) moveOut;
            rMoveOut.setProductName(this.getProductName());
            rMoveOut.setVariationName(this.getVariationName());
            rMoveOut.setParentSku(this.getParentSku());
            rMoveOut.setProductSubtotal(this.getProductSubtotal());
            rMoveOut.setPlatformDiscount(this.getPlatformDiscount());
        }

        return moveOut;
    }

    
}