package com.colbertlum.Imputer.Utils;

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
            if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_COMPLETE) && shopeeOrder.isRequestApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_DELIVERED) && shopeeOrder.isRequestApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_RECEIVED) && shopeeOrder.isRequestApproved()){
                shopeeOrder.setInternalStatus(OrderInternalStatus.AFTER_SALES_RETURN);

            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_DELIVERED)) {
                shopeeOrder.setInternalStatus(OrderInternalStatus.SHIPPING);

            } else if(shopeeOrder.getStatus().contains(ShopeeOrderStatus.STATUS_RECEIVED) && !shopeeOrder.isRequestApproved() ) {
                shopeeOrder.setInternalStatus(OrderInternalStatus.COMPLETED);
                
            } else if(shopeeOrder.getStatus().equals(ShopeeOrderStatus.STATUS_COMPLETE) && !shopeeOrder.isRequestApproved()){
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
            if(tikTokOrder.getStatus().equals(TikTokOrderStatus.COMPLETED) && tikTokOrder.isReturnRefund()) {
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
}
