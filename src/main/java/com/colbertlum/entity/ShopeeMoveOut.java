package com.colbertlum.entity;

public class ShopeeMoveOut extends MoveOut{

    private String productName;
    private String variationName;
    private String parentSku;
    private int foundRow;

    private double productSubtotal;

    @Override
    public double getFinalPrice() {
        ShopeeOrder order = (ShopeeOrder) getOrder();

        double moveOutProportion = productSubtotal / order.getOrderTotalAmount();

        double moveOutTotalFee = order.getManagementFee() * moveOutProportion;
        double moveOutAdjustmentShippingFee = order.getAdjustmentShippingFee() * moveOutProportion;
        double moveOutSellerRebate = order.getSellerRebate() * moveOutProportion;

        return (productSubtotal - moveOutTotalFee - moveOutAdjustmentShippingFee - moveOutSellerRebate) / getQuantity();
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
    public int getFoundRow() {
        return foundRow;
    }
    public void setFoundRow(int foundRow) {
        this.foundRow = foundRow;
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
