package com.colbertlum.entity;

public class ShopeeOrder extends Order {
    private double transactionFee;
    private double commisionFee;
    private double serviceFee;

    private double estimatedShippingFee;
    private double buyerPaidShippingFee;
    private double shippingRebateEstimated;

    private double sellerVoucher;
    private double sellerAbsorbedCoinCashback;

    private double shopeeVoucher;

    private String trackingNumber;

    private String status;
    private boolean requestApproved;

    @Override
    public double getManagementFee(){
        return transactionFee + commisionFee + serviceFee;
    }

    @Override
    public double getAdjustmentShippingFee(){
        return estimatedShippingFee - buyerPaidShippingFee - shippingRebateEstimated;
    }

    @Override
    public double getSellerRebate (){
        return sellerVoucher + (sellerAbsorbedCoinCashback / 100);
    }

    @Override
    public double getPlatformRebate(){
        return 0;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public double getCommisionFee() {
        return commisionFee;
    }

    public void setCommisionFee(double commisionFee) {
        this.commisionFee = commisionFee;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public double getEstimatedShippingFee() {
        return estimatedShippingFee;
    }

    public void setEstimatedShippingFee(double estimatedShippingFee) {
        this.estimatedShippingFee = estimatedShippingFee;
    }

    public double getBuyerPaidShippingFee() {
        return buyerPaidShippingFee;
    }

    public void setBuyerPaidShippingFee(double buyerPaidShippingFee) {
        this.buyerPaidShippingFee = buyerPaidShippingFee;
    }

    public double getShippingRebateEstimated() {
        return shippingRebateEstimated;
    }

    public void setShippingRebateEstimated(double shippingRebateEstimated) {
        this.shippingRebateEstimated = shippingRebateEstimated;
    }

    public double getSellerVoucher() {
        return sellerVoucher;
    }

    public void setSellerVoucher(double sellerVoucher) {
        this.sellerVoucher = sellerVoucher;
    }

    public double getSellerAbsorbedCoinCashback() {
        return sellerAbsorbedCoinCashback;
    }

    public void setSellerAbsorbedCoinCashback(double sellerAbsorbedCoinCashback) {
        this.sellerAbsorbedCoinCashback = sellerAbsorbedCoinCashback;
    }

    public double getShopeeVoucher() {
        return shopeeVoucher;
    }

    public void setShopeeVoucher(double shopeeVoucher) {
        this.shopeeVoucher = shopeeVoucher;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRequestApproved() {
        return requestApproved;
    }

    public void setRequestApproved(boolean requestApproved) {
        this.requestApproved = requestApproved;
    }

    
}
