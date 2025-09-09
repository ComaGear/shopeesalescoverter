package com.colbertlum.entity;

public class ReturnTikTokMoveOut extends ReturnMoveOut {

    private String productName;
    private String variationName;
    private int foundRow;

    private double SKUsubtotalAfterDiscount;
    private double SKUplatformDiscount;
    private double quantity;

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

    public int getFoundRow() {
        return foundRow;
    }

    public void setFoundRow(int foundRow) {
        this.foundRow = foundRow;
    }

    public double getSKUsubtotalAfterDiscount() {
        return SKUsubtotalAfterDiscount;
    }

    public void setSKUsubtotalAfterDiscount(double sKUsubtotalAfterDiscount) {
        SKUsubtotalAfterDiscount = sKUsubtotalAfterDiscount;
    }

    public double getSKUplatformDiscount() {
        return SKUplatformDiscount;
    }

    public void setSKUplatformDiscount(double sKUplatformDiscount) {
        SKUplatformDiscount = sKUplatformDiscount;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public ReturnMoveOut clone() {
        ReturnTikTokMoveOut returnTikTokMoveOut = new ReturnTikTokMoveOut();
        
        copy(returnTikTokMoveOut);

        return returnTikTokMoveOut;
    }
    
    @Override
    public ReturnMoveOut copy(ReturnMoveOut moveOut) {
        super.copy(moveOut);

        if(moveOut instanceof ReturnTikTokMoveOut){
            ReturnTikTokMoveOut rMoveOut = (ReturnTikTokMoveOut) moveOut;
            rMoveOut.setProductName(this.getProductName());
            rMoveOut.setVariationName(this.getVariationName());
            rMoveOut.setFoundRow(this.getFoundRow());
            rMoveOut.setSKUsubtotalAfterDiscount(this.getSKUsubtotalAfterDiscount());
            rMoveOut.setSKUplatformDiscount(this.getSKUplatformDiscount());
            rMoveOut.setQuantity(this.getQuantity());
        }

        return moveOut;
    }
    
}
