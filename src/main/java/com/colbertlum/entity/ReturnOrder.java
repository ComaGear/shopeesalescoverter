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
    private double transactionFee;
    private double orderTotalAmount;
    private double shippingFee;
    private double shopeeVoucher;
    private LocalDate shipOutDate;
    private String status;
    private double serviceFee;
    private double commissionFee;
    private double shippingRebateEstimate; 

    private List<SoftReference<ReturnMoveOut>> returnMoveOutList;
    private LocalDate orderCreationDate;
    private LocalDate orderCompleteDate;
    private String trackingNumber;
    private boolean requestApproved;
    

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

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getShopeeVoucher() {
        return shopeeVoucher;
    }

    public void setShopeeVoucher(double shopeeVoucher) {
        this.shopeeVoucher = shopeeVoucher;
    }

    public LocalDate getShipOutDate() {
        return shipOutDate;
    }

    public void setShipOutDate(LocalDate shipOutDate) {
        this.shipOutDate = shipOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public double getCommissionFee() {
        return commissionFee;
    }

    public void setCommissionFee(double commissionFee) {
        this.commissionFee = commissionFee;
    }

    public double getShippingRebateEstimate() {
        return shippingRebateEstimate;
    }

    public void setShippingRebateEstimate(double shippingRebateEstimate) {
        this.shippingRebateEstimate = shippingRebateEstimate;
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

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public boolean isRequestApproved() {
        return requestApproved;
    }

    public void setRequestApproved(boolean requestApproved) {
        this.requestApproved = requestApproved;
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
        this.transactionFee = order.getTransactionFee();
        this.orderTotalAmount = order.getOrderTotalAmount();
        this.shippingFee = order.getShippingFee();
        this.shopeeVoucher = order.getShopeeVoucher();
        this.shipOutDate = order.getShipOutDate();
        this.status = order.getStatus();
        this.serviceFee = order.getServiceFee();
        this.commissionFee = order.getCommissionFee();
        this.shippingRebateEstimate = order.getShippingRebateEstimate();
        this.orderCreationDate = order.getOrderCreationDate();
        this.orderCompleteDate = order.getOrderCompleteDate();
        this.trackingNumber = order.getTrackingNumber();
        this.requestApproved = order.isRequestApproved();
    }

    public ReturnOrder(){
    }

    public ReturnOrder clone(ArrayList<ReturnMoveOut> returnMoveOuts) {
        
        ReturnOrder clone = new ReturnOrder();
        clone.setId(getId());
        clone.setManagementFee(getManagementFee());
        clone.setOrderTotalAmount(getOrderTotalAmount());
        clone.setShippingFee(getShippingFee());
        clone.setShopeeVoucher(getShopeeVoucher());
        clone.setShipOutDate(getShipOutDate());
        clone.setStatus(getStatus());
        clone.setServiceFee(getServiceFee());
        clone.setCommissionFee(getCommissionFee());
        clone.setShippingRebateEstimate(getShippingRebateEstimate());
        clone.setOrderCreationDate(getOrderCreationDate());
        clone.setOrderCompleteDate(getOrderCompleteDate());
        clone.setTrackingNumber(getTrackingNumber());
        clone.setRequestApproved(isRequestApproved());
        clone.setReturnType(getReturnType());

        clone.setReturnMoveOutList(new ArrayList<SoftReference<ReturnMoveOut>>(getReturnMoveOutList().size()));
        for(SoftReference<ReturnMoveOut> softMoveOut : getReturnMoveOutList()){
            
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
        this.setShippingFee(clone.getShippingFee());
        this.setShopeeVoucher(clone.getShopeeVoucher());
        this.setShipOutDate(clone.getShipOutDate());
        this.setStatus(clone.getStatus());
        this.setServiceFee(clone.getServiceFee());
        this.setCommissionFee(clone.getCommissionFee());
        this.setShippingRebateEstimate(clone.getShippingRebateEstimate());
        this.setOrderCreationDate(clone.getOrderCreationDate());
        this.setOrderCompleteDate(clone.getOrderCompleteDate());
        this.setTrackingNumber(clone.getTrackingNumber());
        this.setRequestApproved(clone.isRequestApproved());
        this.setReturnType(clone.getReturnType());


        
        // just use new list from updated list.
        this.setReturnMoveOutList(clone.getReturnMoveOutList());
        
        // Comparator<SoftReference<ReturnMoveOut>> comparator = new Comparator<SoftReference<ReturnMoveOut>>() {

        //     @Override
        //     public int compare(SoftReference<ReturnMoveOut> o1, SoftReference<ReturnMoveOut> o2) {
        //         if(!o1.get().getSku().equals(o2.get().getSku())){
        //             return o1.get().getSku().compareTo(o2.get().getSku());
        //         }
        //         if(!o1.get().getProductName().equals(o2.get().getProductName())){
        //             return o1.get().getProductName().compareTo(o2.get().getProductName());
        //         }
        //         if(!o1.get().getVariationName().equals(o2.get().getVariationName())){
        //             return o1.get().getVariationName().compareTo(o2.get().getVariationName());
        //         }
        //         if(o1.get().getQuantity() != o2.get().getQuantity()){
        //             if(o1.get().getQuantity() > o2.get().getQuantity()) return 1;
        //             if(o1.get().getQuantity() < o2.get().getQuantity()) return -1;
        //         }
        //         if(o1.get().getPrice() != o2.get().getPrice()){
        //             if(o1.get().getPrice() > o2.get().getPrice()) return 1;
        //             if(o1.get().getPrice() < o2.get().getPrice()) return -1;
        //         }
        //         return 0;
        //     }
            
        // };
        // clone.getReturnMoveOutList().sort(comparator);
        // getReturnMoveOutList().sort(comparator);
        // for(int i = 0; i < getReturnMoveOutList().size(); i++){
            

        //     ReturnMoveOut thisReturnMoveOut = getReturnMoveOutList().get(i).get();
        //     ReturnMoveOut cloneReturnMoveOut = clone.getReturnMoveOutList().get(i).get();
            
        //     thisReturnMoveOut.setSku(cloneReturnMoveOut.getSku());
        //     thisReturnMoveOut.setProductName(cloneReturnMoveOut.getProductName());
        //     thisReturnMoveOut.setVariationName(cloneReturnMoveOut.getVariationName());
        //     thisReturnMoveOut.setQuantity(cloneReturnMoveOut.getQuantity());
        //     thisReturnMoveOut.setOrderId(cloneReturnMoveOut.getOrderId());
        //     thisReturnMoveOut.setPrice(cloneReturnMoveOut.getPrice());
        //     thisReturnMoveOut.setOrderId(cloneReturnMoveOut.getOrderId());
        //     thisReturnMoveOut.setReturnStatus(cloneReturnMoveOut.getReturnStatus());
        //     thisReturnMoveOut.setStatusQuantity(cloneReturnMoveOut.getStatusQuantity());
        // }

    }

    
    
}
