package com.colbertlum.entity;

import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnOrder {

    public static final String FAILED_DELIVERY_TYPE = "Failed Delivery or Return After Ship Out";
    public static final String REQUEST_RETURN_REFUND = "Request Return/Refund or Return After Completed";

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

    private double returnShippingFee;

    private List<SoftReference<ReturnMoveOut>> returnMoveOutList;

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

    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public LocalDate getShipOutDate() {
        return shipOutDate;
    }

    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
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

    public String getReturnType() {

        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public ReturnOrder(Order order) {
        this.id = order.getId();
        this.managementFee = order.getManagementFee();
        this.orderTotalAmount = order.getOrderTotalAmount();
        this.shipOutDate = order.getShipOutDate();
        this.orderCreationDate = order.getOrderCreationDate();
        this.orderCompleteDate = order.getOrderCompleteDate();
        this.adjustmentshipppingFee = order.getAdjustmentShippingFee();
        this.sellerRebate = order.getSellerRebate();
        this.internalStatus = order.getInternalStatus();
    }

    public ReturnOrder() {
    }

    public ReturnOrder clone(List<ReturnMoveOut> returnMoveOuts) {

        ReturnOrder clone = new ReturnOrder();
        clone.setId(getId());
        clone.setManagementFee(getManagementFee());
        clone.setOrderTotalAmount(getOrderTotalAmount());
        clone.setShipOutDate(getShipOutDate());
        clone.setOrderCreationDate(getOrderCreationDate());
        clone.setOrderCompleteDate(getOrderCompleteDate());
        clone.setReturnType(getReturnType());
        clone.setAdjustmentshipppingFee(getAdjustmentshipppingFee());
        clone.setSellerRebate(getSellerRebate());
        clone.setInternalStatus(getInternalStatus());

        clone.setReturnMoveOutList(new ArrayList<SoftReference<ReturnMoveOut>>(getReturnMoveOutList().size()));
        for (SoftReference<ReturnMoveOut> softMoveOut : getReturnMoveOutList()) {

            ReturnMoveOut cloneReturnMove = softMoveOut.get().clone();
            returnMoveOuts.add(cloneReturnMove);
            clone.getReturnMoveOutList().add(new SoftReference<ReturnMoveOut>(cloneReturnMove));
        }

        return clone;
    }

    public void update(ReturnOrder clone) {
        this.setId(clone.getId());
        this.setManagementFee(clone.getManagementFee());
        this.setOrderTotalAmount(clone.getOrderTotalAmount());
        this.setShipOutDate(clone.getShipOutDate());
        this.setOrderCreationDate(clone.getOrderCreationDate());
        this.setOrderCompleteDate(clone.getOrderCompleteDate());
        this.setReturnType(clone.getReturnType());
        this.setAdjustmentshipppingFee(clone.getAdjustmentshipppingFee());
        this.setSellerRebate(clone.getSellerRebate());
        this.setInternalStatus(clone.getInternalStatus());

        // just use new list from updated list.
        this.setReturnMoveOutList(clone.getReturnMoveOutList());
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

    public double getReturnShippingFee() {
        return returnShippingFee;
    }

    public void setReturnShippingFee(double returnShippingFee) {
        this.returnShippingFee = returnShippingFee;
    }

}
