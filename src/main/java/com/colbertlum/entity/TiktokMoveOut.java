package com.colbertlum.entity;

public class TikTokMoveOut extends MoveOut {

    private String productName;
    private String variationName;

    private double SKUsubtotalAfterDiscount;
    private double SKUplatformDiscount;

    @Override
    public String getName() {
        return productName + "-" + variationName;
    }

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
