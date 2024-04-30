package com.colbertlum.entity;

import java.time.LocalDate;

public class SummaryOrder {
    private String id;
    private double profit;
    private double totalAmount;
    private LocalDate shipOutDate;

    public LocalDate getShipOutDate() {
        return shipOutDate;
    }

    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
