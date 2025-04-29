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

}
