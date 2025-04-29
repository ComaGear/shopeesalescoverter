package com.colbertlum.entity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class ReturnShopeeOrder extends ReturnOrder {
    private double transactionFee;
    private double commissionFee;
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
    public void update(ReturnOrder clone) {
        super.update(clone);
        if(clone instanceof ReturnShopeeOrder) update(clone);
    }

    public void update(ReturnShopeeOrder clone) {
        this.setTransactionFee(clone.getTransactionFee());
        this.setCommissionFee(clone.getCommissionFee());
        this.setServiceFee(clone.getServiceFee());
        this.setEstimatedShippingFee(clone.getEstimatedShippingFee());
        this.setBuyerPaidShippingFee(clone.getBuyerPaidShippingFee());
        this.setShippingRebateEstimated(clone.getShippingRebateEstimated());
        this.setSellerVoucher(clone.getSellerVoucher());
        this.setSellerAbsorbedCoinCashback(clone.getSellerAbsorbedCoinCashback());
        this.setShopeeVoucher(clone.getShopeeVoucher());
        this.setTrackingNumber(clone.getTrackingNumber());
        this.setStatus(clone.getStatus());
        this.setRequestApproved(clone.isRequestApproved());

        this.setReturnMoveOutList(clone.getReturnMoveOutList());
    }

    @Override
    public ReturnOrder clone(List<ReturnMoveOut> returnMoveOuts) {

        ReturnShopeeOrder cloneReturnShopeeOrder = new ReturnShopeeOrder();
        cloneReturnShopeeOrder.update(this);
        cloneReturnShopeeOrder.setReturnMoveOutList(new ArrayList<SoftReference<ReturnMoveOut>>(getReturnMoveOutList().size()));
        for (SoftReference<ReturnMoveOut> softMoveOut : getReturnMoveOutList()) {

            ReturnMoveOut returnMoveOut = softMoveOut.get();
            if(returnMoveOut instanceof ReturnShopeeMoveOut) {
                ReturnMoveOut cloneReturnMove = ((ReturnShopeeMoveOut) returnMoveOut).clone();
                returnMoveOuts.add(cloneReturnMove);
                cloneReturnShopeeOrder.getReturnMoveOutList().add(new SoftReference<ReturnMoveOut>(cloneReturnMove));
            }
        }

        return cloneReturnShopeeOrder;
    }

    public ReturnShopeeOrder(Order order) {
        super(order);
        if(order instanceof ShopeeOrder) {
            ShopeeOrder shopeeOrder = (ShopeeOrder) order;
            this.setTransactionFee(shopeeOrder.getTransactionFee());
            this.setCommissionFee(shopeeOrder.getCommissionFee());
            this.setServiceFee(shopeeOrder.getServiceFee());
            this.setEstimatedShippingFee(shopeeOrder.getEstimatedShippingFee());
            this.setBuyerPaidShippingFee(shopeeOrder.getBuyerPaidShippingFee());
            this.setShippingRebateEstimated(shopeeOrder.getShippingRebateEstimated());
            this.setSellerVoucher(shopeeOrder.getSellerVoucher());
            this.setSellerAbsorbedCoinCashback(shopeeOrder.getSellerAbsorbedCoinCashback());
            this.setShopeeVoucher(shopeeOrder.getShopeeVoucher());
            this.setTrackingNumber(shopeeOrder.getTrackingNumber());
            this.setStatus(shopeeOrder.getStatus());
            this.setRequestApproved(shopeeOrder.isRequestApproved());
        }
    }

    public ReturnShopeeOrder() {

    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public double getCommissionFee() {
        return commissionFee;
    }

    public void setCommissionFee(double commissionFee) {
        this.commissionFee = commissionFee;
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
