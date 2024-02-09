package com.colbertlum.entity;

import java.time.LocalDate;

public class Order {

    public static final String STATUS_CANCEL = "Cancelled";
    public static final String STATUS_UNPAID = "Unpaid";
    public static final String STATUS_TO_SHIP = "To ship";

    private String id;
    private double managementFee;
    private double transactionFee;
    private double orderTotalAmount;
    private double shippingFee;
    private double shopeeVoucher;
    private LocalDate shipOutDate;
    private String status;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getShipOutDate() {
        return shipOutDate;
    }
    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
    }
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
