package com.colbertlum.entity;

public class TiktokMoveOut extends MoveOut {

    private String productName;
    private String variationName;
    private int foundRow;

    private double SKUsubtotalAfterDiscount;
    private double SKUplatformDiscount;
    private double quantity;

    @Override
    public double getFinalPrice() {
        TikTokOrder order = (TikTokOrder) getOrder();

        double moveOutSubtotal = SKUsubtotalAfterDiscount + SKUplatformDiscount;
        double moveOutProportion = moveOutSubtotal / order.getTotalRevenue();

        double moveOutTotalFee = order.getManagementFee() * moveOutProportion;
        double moveOutAdjustmentShippingFee = order.getAdjustmentShippingFee() * moveOutProportion;
        double moveOutSellerRebate = order.getSellerRebate() * moveOutProportion;

        return (moveOutSubtotal - moveOutTotalFee - moveOutAdjustmentShippingFee - moveOutSellerRebate) / getQuantity();
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
    public String toString() {
        return new StringBuilder()
            .append("MoveOut@")
            .append("'Order ID: " + getOrder().getId() + "', ")
            .append("'sku : " + getSku() + "', ")
            .append("'Name' : " + getName() + "', ")
            .append("'variation' : " + getVariationName() + "',")
            .append("'QTY : " + getQuantity() + "', ")
            .append("'Price : " + getPrice() + "', ")
            .append("; /n")
            .toString();
    }
}
