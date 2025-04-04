package com.colbertlum.entity;

public class ShopeeOrder extends Order {
    private double transactionFee;
    private double commisionFe;
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
}
