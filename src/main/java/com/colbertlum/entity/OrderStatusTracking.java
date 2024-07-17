package com.colbertlum.entity;

import java.time.LocalDate;
import java.util.List;

public class OrderStatusTracking {

    public static String CANCEL_REASON_FAILED_DELIVERY = "Cancelled automatically by Shopee's system. Reason: Failed delivery";

    private LocalDate orderCompletedTime;
    private String orderId;
    private LocalDate shipOutDate;
    private boolean requestApproved;
    private String trackingNumber;
    private String reportStatus;
    private boolean cancelled;

    private String internalStatus;

    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    private List<ItemMovementStatus> itemMovementStatusList;

    public List<ItemMovementStatus> getItemMovementStatusList() {
        return itemMovementStatusList;
    }

    public void setItemMovementStatusList(List<ItemMovementStatus> itemMovementStatusList) {
        this.itemMovementStatusList = itemMovementStatusList;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public LocalDate getShipOutDate() {
        return shipOutDate;
    }

    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
    }

    public boolean isRequestApproved() {
        return requestApproved;
    }

    public void setRequestApproved(boolean requestApproved) {
        this.requestApproved = requestApproved;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setOrderCompletedTime(LocalDate orderCompletedTime) {
        this.orderCompletedTime = orderCompletedTime;
    }

    public boolean hasCompleteTime() {
        return orderCompletedTime == null ? false : true;
    }

}
