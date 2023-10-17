package com.colbertlum.entity;

public class Order {
    private String id;
    private double managementFee;
    private double transactionFee;
    private double orderTotalAmount;
    private double shippingFee;
    private double shopeeVoucher;

    public double getShopeeVoucher() {
        return shopeeVoucher;
    }
    public void setShopeeVoucher(double shopeeVoucher) {
        this.shopeeVoucher = shopeeVoucher;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public double getManagementFee() {
        return managementFee;
    }
    public void setManagementFee(double managementFee) {
        this.managementFee = managementFee;
    }
    public double getTransactionFee() {
        return transactionFee;
    }
    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }
    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }
    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }
    public double getShippingFee() {
        return shippingFee;
    }
    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

}
