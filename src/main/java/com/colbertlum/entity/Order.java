package com.colbertlum.entity;

import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.List;

public class Order {

    public static final String STATUS_CANCEL = "Cancelled";
    public static final String STATUS_UNPAID = "Unpaid";
    public static final String STATUS_TO_SHIP = "To ship";
    public static final String STATUS_COMPLETED = "Completed";

    public static String CANCEL_REASON_FAILED_DELIVERY = "Cancelled automatically by Shopee's system. Reason: Failed delivery";

    private String id;
    private double managementFee;
    private double transactionFee;
    private double orderTotalAmount;
    private double shippingFee;
    private double shopeeVoucher;
    private LocalDate shipOutDate;
    private String status;
    private double serviceFee;
    private double commissionFee;
    private double shippingRebateEstimate; 

    private List<SoftReference<MoveOut>> MoveOutList;
    private LocalDate orderCreationDate;
    private LocalDate orderCompleteDate;
    private String trackingNumber;
    private boolean requestApproved;
    
    public List<SoftReference<MoveOut>> getMoveOutList() {
        return MoveOutList;
    }
    public void setMoveOutList(List<SoftReference<MoveOut>> moveOutList) {
        MoveOutList = moveOutList;
    }
    public LocalDate getOrderCreationDate() {
        return orderCreationDate;
    }
    public void setOrderCreationDate(LocalDate orderCreationDate) {
        this.orderCreationDate = orderCreationDate;
    }
    public LocalDate getOrderCompleteDate() {
        return orderCompleteDate;
    }
    public void setOrderCompleteDate(LocalDate orderCompleteDate) {
        this.orderCompleteDate = orderCompleteDate;
    }
    public String getTrackingNumber() {
        return trackingNumber;
    }
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    public boolean isRequestApproved() {
        return requestApproved;
    }
    public void setRequestApproved(boolean requestApproved) {
        this.requestApproved = requestApproved;
    }
    public double getShippingRebateEstimate() {
        return shippingRebateEstimate;
    }
    public void setShippingRebateEstimate(double shippingRebateEstimate) {
        this.shippingRebateEstimate = shippingRebateEstimate;
    }
    public double getServiceFee() {
        return serviceFee;
    }
    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }


    public double getCommissionFee() {
        return commissionFee;
    }
    public void setCommissionFee(double commissionFee) {
        this.commissionFee = commissionFee;
    }
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
