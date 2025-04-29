package com.colbertlum.entity;

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
}
