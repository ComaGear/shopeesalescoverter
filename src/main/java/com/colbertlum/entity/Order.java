package com.colbertlum.entity;

import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.List;

public class Order {

    // public static final String STATUS_CANCEL = "Cancelled";
    // public static final String STATUS_UNPAID = "Unpaid";
    // public static final String STATUS_TO_SHIP = "To ship";
    // public static final String STATUS_COMPLETED = "Completed";

    // public static String CANCEL_REASON_FAILED_DELIVERY = "Cancelled automatically by Shopee's system. Reason: Failed delivery";

    private String id;
    private double managementFee;
    private double adjustmentshipppingFee;
    private double sellerRebate;
    private double platformRebate;

    private double orderTotalAmount;

    private String internalStatus;

    private LocalDate shipOutDate;
    private LocalDate orderCreationDate;
    private LocalDate orderCompleteDate;

    private List<SoftReference<MoveOut>> MoveOutList;

    
    
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
    public LocalDate getShipOutDate() {
        return shipOutDate;
    }
    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
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
    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }
    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }
    public double getAdjustmentshipppingFee() {
        return adjustmentshipppingFee;
    }
    public void setAdjustmentshipppingFee(double adjustmentshipppingFee) {
        this.adjustmentshipppingFee = adjustmentshipppingFee;
    }
    public double getSellerRebate() {
        return sellerRebate;
    }
    public void setSellerRebate(double sellerRebate) {
        this.sellerRebate = sellerRebate;
    }
    public double getPlatformRebate() {
        return platformRebate;
    }
    public void setPlatformRebate(double platformRebate) {
        this.platformRebate = platformRebate;
    }
    public String getInternalStatus() {
        return internalStatus;
    }
    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    public Order(){

    }

    public Order(String id) {
        this.setId(id);
    }

}
