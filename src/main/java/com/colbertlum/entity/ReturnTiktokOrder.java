package com.colbertlum.entity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class ReturnTiktokOrder extends ReturnOrder {

    private double transactionFee;
    private double tiktokShopCommisionFee;
    private double SFPserviceFee;

    private double platformShippingFeeDiscount;
    private double customerPaidShippingFee;
    private double actualShippingFee;
    private double sellerShippingFee;

    private double affiliateShopAdsCommision;
    private double affiliateCommision;
    private double affiliatePartnerCommision;

    private double totalSettlementAmount;
    private double totalRevenue;

    private double sellerDiscount;

    private String trackingNumber;


    @Override
    public void update(ReturnOrder clone) {
        super.update(clone);
        if(clone instanceof ReturnShopeeOrder) update(clone);
    }

    public void update(ReturnTiktokOrder clone) {
        this.setTransactionFee(clone.getTransactionFee());
        this.setTiktokShopCommisionFee(clone.getTiktokShopCommisionFee());
        this.setSFPserviceFee(clone.getSFPserviceFee());
        this.setPlatformShippingFeeDiscount(clone.getPlatformShippingFeeDiscount());
        this.setCustomerPaidShippingFee(clone.getCustomerPaidShippingFee());
        this.setActualShippingFee(clone.getActualShippingFee());
        this.setAffiliateCommision(clone.getAffiliateCommision());
        this.setAffiliatePartnerCommision(clone.getAffiliatePartnerCommision());
        this.setAffiliateShopAdsCommision(clone.getAffiliateShopAdsCommision());
        this.setTotalSettlementAmount(clone.getTotalSettlementAmount());
        this.setTotalRevenue(clone.getTotalRevenue());
        this.setSellerDiscount(clone.getSellerDiscount());
        this.setSellerShippingFee(clone.getSellerShippingFee());

        this.setReturnMoveOutList(clone.getReturnMoveOutList());
    }

    @Override
    public ReturnOrder clone(List<ReturnMoveOut> returnMoveOuts) {

        ReturnTiktokOrder cloneReturnTiktokOrder = new ReturnTiktokOrder();
        cloneReturnTiktokOrder.update(this);
        cloneReturnTiktokOrder.setReturnMoveOutList(new ArrayList<SoftReference<ReturnMoveOut>>(getReturnMoveOutList().size()));
        for (SoftReference<ReturnMoveOut> softMoveOut : getReturnMoveOutList()) {

            ReturnMoveOut returnMoveOut = softMoveOut.get();
            if(returnMoveOut instanceof ReturnTikTokMoveOut) {
                ReturnMoveOut cloneReturnMove = ((ReturnTikTokMoveOut) returnMoveOut).clone();
                returnMoveOuts.add(cloneReturnMove);
                cloneReturnTiktokOrder.getReturnMoveOutList().add(new SoftReference<ReturnMoveOut>(cloneReturnMove));
            }
        }

        return cloneReturnTiktokOrder;
    }

    public ReturnTiktokOrder(Order order) {
        super(order);
        if(order instanceof TikTokOrder) {
            TikTokOrder tiktokOrder = (TikTokOrder) order;
            this.setTransactionFee(tiktokOrder.getTransactionFee());
            this.setTiktokShopCommisionFee(tiktokOrder.getTiktokShopCommisionFee());
            this.setSFPserviceFee(tiktokOrder.getSFPserviceFee());
            this.setPlatformShippingFeeDiscount(tiktokOrder.getPlatformShippingFeeDiscount());
            this.setCustomerPaidShippingFee(tiktokOrder.getCustomerPaidShippingFee());
            this.setActualShippingFee(tiktokOrder.getActualShippingFee());
            this.setAffiliateCommision(tiktokOrder.getAffiliateCommision());
            this.setAffiliatePartnerCommision(tiktokOrder.getAffiliatePartnerCommision());
            this.setAffiliateShopAdsCommision(tiktokOrder.getAffiliateShopAdsCommision());
            this.setTotalSettlementAmount(tiktokOrder.getTotalSettlementAmount());
            this.setTotalRevenue(tiktokOrder.getTotalRevenue());
            this.setSellerDiscount(tiktokOrder.getSellerDiscount());
            this.setSellerShippingFee(tiktokOrder.getSellerShippingFee());
        }
    }

    public ReturnTiktokOrder() {

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

    public double getSellerShippingFee() {
        return sellerShippingFee;
    }

    public void setSellerShippingFee(double sellerShippingFee) {
        this.sellerShippingFee = sellerShippingFee;
    }
    
}
