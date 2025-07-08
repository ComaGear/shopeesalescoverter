package com.colbertlum.entity;

public class ShopeeOrder extends Order {
    private double transactionFee;
    private double commissionFee;
    private double serviceFee;

    private double actualShippingFee;
    private double estimatedShippingFee;
    private double buyerPaidShippingFee;
    private double shippingRebateEstimated;

    private double sellerVoucher;
    private double sellerAbsorbedCoinCashback;

    private double shopeeVoucher;

    private String trackingNumber;

    private String status;
    private boolean requestApproved;

    private double returnToSellerFee;
    private double sellerPaidShippingFeeSST;
    private double reverseShippingFee;
    private double reverseShippingFeeSST;
    private double shopeeRebate;
    private double amsCommisionFee;
    private double saverProgrammeFee;
    private double buyerPaidInstallationFee;
    private double actualInstallationFee;

    @Override
    public double getManagementFee(){
        return transactionFee + commissionFee + serviceFee;
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

    public double getCommissionFee() {
        return commissionFee;
    }

    public void setCommissionFee(double commisionFee) {
        this.commissionFee = commisionFee;
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

    public double getReturnToSellerFee() {
        return returnToSellerFee;
    }

    public void setReturnToSellerFee(double returnToSellerFee) {
        this.returnToSellerFee = returnToSellerFee;
    }

    public double getSellerPaidShippingFeeSST() {
        return sellerPaidShippingFeeSST;
    }

    public void setSellerPaidShippingFeeSST(double sellerPaidShippingFeeSST) {
        this.sellerPaidShippingFeeSST = sellerPaidShippingFeeSST;
    }

    public double getReverseShippingFee() {
        return reverseShippingFee;
    }

    public void setReverseShippingFee(double reverseShippingFee) {
        this.reverseShippingFee = reverseShippingFee;
    }

    public double getReverseShippingFeeSST() {
        return reverseShippingFeeSST;
    }

    public void setReverseShippingFeeSST(double reverseShippingFeeSST) {
        this.reverseShippingFeeSST = reverseShippingFeeSST;
    }

    public double getShopeeRebate() {
        return shopeeRebate;
    }

    public void setShopeeRebate(double shopeeRebate) {
        this.shopeeRebate = shopeeRebate;
    }

    public double getAmsCommisionFee() {
        return amsCommisionFee;
    }

    public void setAmsCommisionFee(double amsCommisionFee) {
        this.amsCommisionFee = amsCommisionFee;
    }

    public double getSaverProgrammeFee() {
        return saverProgrammeFee;
    }

    public void setSaverProgrammeFee(double saverProgrammeFee) {
        this.saverProgrammeFee = saverProgrammeFee;
    }

    public double getBuyerPaidInstallationFee() {
        return buyerPaidInstallationFee;
    }

    public void setBuyerPaidInstallationFee(double buyerPaidInstallationFee) {
        this.buyerPaidInstallationFee = buyerPaidInstallationFee;
    }

    public double getActualInstallationFee() {
        return actualInstallationFee;
    }

    public void setActualInstallationFee(double actualInstallationFee) {
        this.actualInstallationFee = actualInstallationFee;
    }

    public double getActualShippingFee() {
        return actualShippingFee;
    }

    public void setActualShippingFee(double actualShippingFee) {
        this.actualShippingFee = actualShippingFee;
    }
}
