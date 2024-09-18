package com.colbertlum.entity;

import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.List;

public class ReturnOrder {

    public static final String FAILED_DELIVERY_TYPE = "Failed Delivery or Return After Ship Out";
    public static final String REQUEST_RETURN_REFUND = "Request Return/Refund or Return After Completed";

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

    private List<SoftReference<ReturnMoveOut>> returnMoveOutList;
    private LocalDate orderCreationDate;
    private LocalDate orderCompleteDate;
    private String trackingNumber;
    private boolean requestApproved;
    

    private String returnType;
    
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

    public double getShopeeVoucher() {
        return shopeeVoucher;
    }

    public void setShopeeVoucher(double shopeeVoucher) {
        this.shopeeVoucher = shopeeVoucher;
    }

    public LocalDate getShipOutDate() {
        return shipOutDate;
    }

    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public double getShippingRebateEstimate() {
        return shippingRebateEstimate;
    }

    public void setShippingRebateEstimate(double shippingRebateEstimate) {
        this.shippingRebateEstimate = shippingRebateEstimate;
    }

    public List<SoftReference<ReturnMoveOut>> getReturnMoveOutList() {
        return returnMoveOutList;
    }

    public void setReturnMoveOutList(List<SoftReference<ReturnMoveOut>> returnMoveOutList) {
        this.returnMoveOutList = returnMoveOutList;
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

    public String getReturnType() {

        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ReturnOrder(Order order) {
        this.id = order.getId();
        this.managementFee = order.getManagementFee();
        this.transactionFee = order.getTransactionFee();
        this.orderTotalAmount = order.getOrderTotalAmount();
        this.shippingFee = order.getShippingFee();
        this.shopeeVoucher = order.getShopeeVoucher();
        this.shipOutDate = order.getShipOutDate();
        this.status = order.getStatus();
        this.serviceFee = order.getServiceFee();
        this.commissionFee = order.getCommissionFee();
        this.shippingRebateEstimate = order.getShippingRebateEstimate();
        this.orderCreationDate = order.getOrderCreationDate();
        this.orderCompleteDate = order.getOrderCompleteDate();
        this.trackingNumber = order.getTrackingNumber();
        this.requestApproved = order.isRequestApproved();
}

    public ReturnOrder(){
    }
    
}
