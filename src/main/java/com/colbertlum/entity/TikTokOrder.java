package com.colbertlum.entity;

public class TikTokOrder extends Order {
    private double transactionFee;
    private double tiktokShopCommisionFee;
    private double SFPserviceFee;

    private double platformShippingFeeDiscount;
    private double customerPaidShippingFee;
    private double actualShippingFee;

    private double affiliateShopAdsCommision;
    private double affiliateCommision;
    private double affiliatePartnerCommision;

    private double totalSettlementAmount;
    private double totalRevenue;

    private double sellerDiscount;

    private String trackingNumber;

    @Override
    public double getManagementFee(){
        double affiliateTotal = affiliateShopAdsCommision + affiliateCommision + affiliatePartnerCommision;
        double totalFee = transactionFee + tiktokShopCommisionFee + SFPserviceFee;
        return (affiliateTotal + totalFee) * -1;
    }

    @Override
    public double getAdjustmentShippingFee(){
        return platformShippingFeeDiscount + customerPaidShippingFee + actualShippingFee;
    }

    @Override
    public double getSellerRebate(){
        return sellerDiscount;
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

    public double getTiktokShopCommisionFee() {
        return tiktokShopCommisionFee;
    }

    public void setTiktokShopCommisionFee(double tiktokShopCommisionFee) {
        this.tiktokShopCommisionFee = tiktokShopCommisionFee;
    }

    public double getSFPserviceFee() {
        return SFPserviceFee;
    }

    public void setSFPserviceFee(double sFPserviceFee) {
        SFPserviceFee = sFPserviceFee;
    }

    public double getPlatformShippingFeeDiscount() {
        return platformShippingFeeDiscount;
    }

    public void setPlatformShippingFeeDiscount(double platformShippingFeeDiscount) {
        this.platformShippingFeeDiscount = platformShippingFeeDiscount;
    }

    public double getCustomerPaidShippingFee() {
        return customerPaidShippingFee;
    }

    public void setCustomerPaidShippingFee(double customerPaidShippingFee) {
        this.customerPaidShippingFee = customerPaidShippingFee;
    }

    public double getActualShippingFee() {
        return actualShippingFee;
    }

    public void setActualShippingFee(double actualShippingFee) {
        this.actualShippingFee = actualShippingFee;
    }

    public double getAffiliateShopAdsCommision() {
        return affiliateShopAdsCommision;
    }

    public void setAffiliateShopAdsCommision(double affiliateShopAdsCommision) {
        this.affiliateShopAdsCommision = affiliateShopAdsCommision;
    }

    public double getAffiliateCommision() {
        return affiliateCommision;
    }

    public void setAffiliateCommision(double affiliateCommision) {
        this.affiliateCommision = affiliateCommision;
    }

    public double getAffiliatePartnerCommision() {
        return affiliatePartnerCommision;
    }

    public void setAffiliatePartnerCommision(double affiliatePartnerCommision) {
        this.affiliatePartnerCommision = affiliatePartnerCommision;
    }

    public double getTotalSettlementAmount() {
        return totalSettlementAmount;
    }

    public void setTotalSettlementAmount(double totalSettlementAmount) {
        this.totalSettlementAmount = totalSettlementAmount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getSellerDiscount() {
        return sellerDiscount;
    }

    public void setSellerDiscount(double sellerDiscount) {
        this.sellerDiscount = sellerDiscount;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    
}
