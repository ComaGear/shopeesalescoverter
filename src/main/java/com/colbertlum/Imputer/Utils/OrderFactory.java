package com.colbertlum.Imputer.Utils;

import java.util.Comparator;
import java.util.List;

import com.colbertlum.constants.OrderInternalStatus;
import com.colbertlum.constants.ShopeeOrderStatus;
import com.colbertlum.constants.TikTokOrderStatus;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnOrder;
import com.colbertlum.entity.ReturnShopeeOrder;
import com.colbertlum.entity.ReturnTiktokOrder;
import com.colbertlum.entity.ShopeeOrder;
import com.colbertlum.entity.TikTokOrder;

public class OrderFactory {

    private static Comparator<Order> orderIdComparator = new Comparator<Order>() {

            @Override
            public int compare(Order o1, Order o2) {
                return o1.getId().compareTo(o2.getId());
            }
            
        };

    public static String getPlatform(Order Order) {
        if(Order instanceof ShopeeOrder) {
            return "Shopee";
        }
        if(Order instanceof TikTokOrder) {
            return "TikTok";
        }
        return null;
    }
    
    public static String getTrackingNumber(Order order){
        if(order instanceof ShopeeOrder) {
            ShopeeOrder shopeeOrder = (ShopeeOrder) order;
            return shopeeOrder.getTrackingNumber();
        }
        if(order instanceof TikTokOrder) {
            TikTokOrder tikTokOrder = (TikTokOrder) order;
            return tikTokOrder.getTrackingNumber();
        }
        return null;
    }

    public static String getTrackingNumber(ReturnOrder order){
        if(order instanceof ReturnShopeeOrder) {
            ReturnShopeeOrder returnShopeeOrder = (ReturnShopeeOrder) order;
            return returnShopeeOrder.getTrackingNumber();
        }
        if(order instanceof ReturnTiktokOrder) {
            ReturnTiktokOrder returnTiktokOrder = (ReturnTiktokOrder) order;
            return returnTiktokOrder.getTrackingNumber();
        }
        return null;
    }

    public static Order mappingOrderInternalStatus(Order order) {
        if(order instanceof ShopeeOrder) {
            ShopeeOrder shopeeOrder = (ShopeeOrder) order;
            if(shopeeOrder.getSettledDate() != null) {
                shopeeOrder.setInternalStatus(OrderInternalStatus.SETTLED);
                
            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_COMPLETE) && shopeeOrder.isRequestReturnRefundApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_DELIVERED) && shopeeOrder.isRequestReturnRefundApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_RECEIVED) && shopeeOrder.isRequestReturnRefundApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_DELIVERED)) {
                shopeeOrder.setInternalStatus(OrderInternalStatus.SHIPPING);

            } else if(shopeeOrder.getStatus().contains(ShopeeOrderStatus.STATUS_RECEIVED) && !shopeeOrder.isRequestReturnRefundApproved() ) {
                shopeeOrder.setInternalStatus(OrderInternalStatus.COMPLETED);
                
            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_COMPLETE) && !shopeeOrder.isRequestReturnRefundApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.COMPLETED);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_SHIPPING)){
                shopeeOrder.setInternalStatus(OrderInternalStatus.SHIPPING);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_CANCEL) && shopeeOrder.getShipOutDate() != null){
                shopeeOrder.setInternalStatus(OrderInternalStatus.RETURNING);
                
            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_TO_SHIP)){
                shopeeOrder.setInternalStatus(OrderInternalStatus.PENDING);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.CANCELLED)) {
                shopeeOrder.setInternalStatus(OrderInternalStatus.CANCELLED);

            }
            return shopeeOrder;
        }
        if(order instanceof TikTokOrder) {
            TikTokOrder tikTokOrder = (TikTokOrder) order;
            if(tikTokOrder.getSettledDate() != null) {
                tikTokOrder.setInternalStatus(OrderInternalStatus.SETTLED);

            } else if(tikTokOrder.getStatus().equals(TikTokOrderStatus.COMPLETED) && tikTokOrder.isReturnRefund()) {
                tikTokOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(tikTokOrder.getStatus().equals(TikTokOrderStatus.CANCELLED) && tikTokOrder.getShipOutDate() != null) {
                tikTokOrder.setInternalStatus(OrderInternalStatus.RETURNING);

            } else if(tikTokOrder.getStatus().equals(TikTokOrderStatus.SHIPPED)) {
                tikTokOrder.setInternalStatus(OrderInternalStatus.SHIPPING);

            } else if(tikTokOrder.getStatus().equals(TikTokOrderStatus.COMPLETED) && !tikTokOrder.isReturnRefund()) {
                tikTokOrder.setInternalStatus(OrderInternalStatus.COMPLETED);

            } else if(tikTokOrder.getStatus().equals(TikTokOrderStatus.CANCELLED)) {
                tikTokOrder.setInternalStatus(OrderInternalStatus.CANCELLED);
            }
            return tikTokOrder;
        }
        return null;
    }

    public static void updatePlatformOrder(Order toUpdateOrder, Order order){
        if(order instanceof ShopeeOrder && toUpdateOrder instanceof ShopeeOrder) {
            ShopeeOrder shopeeOrder = (ShopeeOrder) order;
            ShopeeOrder toUpdateShopeeOrder = (ShopeeOrder) toUpdateOrder;

            toUpdateShopeeOrder.setTransactionFee(shopeeOrder.getTransactionFee());
            toUpdateShopeeOrder.setCommissionFee(shopeeOrder.getCommissionFee());
            toUpdateShopeeOrder.setServiceFee(shopeeOrder.getServiceFee());
            toUpdateShopeeOrder.setActualShippingFee(shopeeOrder.getActualShippingFee());
            toUpdateShopeeOrder.setEstimatedShippingFee(shopeeOrder.getEstimatedShippingFee());
            toUpdateShopeeOrder.setBuyerPaidShippingFee(shopeeOrder.getBuyerPaidShippingFee());
            toUpdateShopeeOrder.setShippingRebateEstimated(shopeeOrder.getShippingRebateEstimated());
            toUpdateShopeeOrder.setSellerVoucher(shopeeOrder.getSellerVoucher());
            toUpdateShopeeOrder.setSellerAbsorbedCoinCashback(shopeeOrder.getSellerAbsorbedCoinCashback());
            toUpdateShopeeOrder.setShopeeVoucher(shopeeOrder.getShopeeVoucher());
            toUpdateShopeeOrder.setTrackingNumber(shopeeOrder.getTrackingNumber());
            toUpdateShopeeOrder.setStatus(toUpdateShopeeOrder.getStatus());
            toUpdateShopeeOrder.setRequestApproved(shopeeOrder.isRequestApproved());
            toUpdateShopeeOrder.setReturnToSellerFee(shopeeOrder.getReturnToSellerFee());
            toUpdateShopeeOrder.setSellerPaidShippingFeeSST(shopeeOrder.getSellerPaidShippingFeeSST());
            toUpdateShopeeOrder.setReverseShippingFee(shopeeOrder.getReverseShippingFee());
            toUpdateShopeeOrder.setReverseShippingFeeSST(shopeeOrder.getReverseShippingFeeSST());
            toUpdateShopeeOrder.setShopeeRebate(shopeeOrder.getShopeeRebate());
            toUpdateShopeeOrder.setAmsCommisionFee(shopeeOrder.getAmsCommisionFee());
            toUpdateShopeeOrder.setSaverProgrammeFee(shopeeOrder.getSaverProgrammeFee());
            toUpdateShopeeOrder.setBuyerPaidInstallationFee(shopeeOrder.getBuyerPaidInstallationFee());
            toUpdateShopeeOrder.setActualInstallationFee(shopeeOrder.getActualInstallationFee());
        }
        if(order instanceof TikTokOrder && toUpdateOrder instanceof TikTokOrder) {
            TikTokOrder tikTokOrder = (TikTokOrder) order;
            TikTokOrder toUpdateTikTokOrder = (TikTokOrder) toUpdateOrder;

            toUpdateTikTokOrder.setTransactionFee(tikTokOrder.getTransactionFee());
            toUpdateTikTokOrder.setTiktokShopCommisionFee(tikTokOrder.getTiktokShopCommisionFee());
            toUpdateTikTokOrder.setSFPserviceFee(tikTokOrder.getSFPserviceFee());
            toUpdateTikTokOrder.setPlatformShippingFeeDiscount(tikTokOrder.getPlatformShippingFeeDiscount());
            toUpdateTikTokOrder.setCustomerPaidShippingFee(tikTokOrder.getCustomerPaidShippingFee());
            toUpdateTikTokOrder.setActualShippingFee(tikTokOrder.getActualShippingFee());
            toUpdateTikTokOrder.setSellerShippingFee(tikTokOrder.getSellerShippingFee());
            toUpdateTikTokOrder.setAffiliateShopAdsCommision(tikTokOrder.getAffiliateShopAdsCommision());
            toUpdateTikTokOrder.setAffiliateCommision(tikTokOrder.getAffiliateCommision());
            toUpdateTikTokOrder.setAffiliatePartnerCommision(tikTokOrder.getAffiliatePartnerCommision());
            toUpdateTikTokOrder.setTotalSettlementAmount(tikTokOrder.getTotalSettlementAmount());
            toUpdateTikTokOrder.setTotalRevenue(tikTokOrder.getTotalRevenue());
            toUpdateTikTokOrder.setSellerDiscount(tikTokOrder.getSellerDiscount());
            toUpdateTikTokOrder.setTrackingNumber(tikTokOrder.getTrackingNumber());
            toUpdateTikTokOrder.setStatus(tikTokOrder.getStatus());
            toUpdateTikTokOrder.setReturnRefund(tikTokOrder.isReturnRefund());
        }
    }

    public static void bindToSingleInstanceOrder(List<Order> orders, List<? extends Order> platformOrders){
        orders.sort(orderIdComparator);
        platformOrders.sort(orderIdComparator);

        for(Order platfomOrder : platformOrders) {
            Order lookupOrder = Lookup.lookupOrder(orders, platfomOrder.getId());
            updatePlatformOrder(lookupOrder, platfomOrder);
        }
    }
}