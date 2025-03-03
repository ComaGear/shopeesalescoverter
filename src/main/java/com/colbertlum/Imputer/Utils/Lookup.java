package com.colbertlum.Imputer.Utils;

import java.util.List;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ProductStock;
import com.colbertlum.entity.ReturnOrder;

public class Lookup {

    public static Order lookupOrder(List<Order> list, String orderId){
        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(list.get(mid).getId().compareTo(orderId) > 0) hi = mid-1;
            else if(list.get(mid).getId().compareTo(orderId) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }

    public static ReturnOrder lookupReturnOrder(List<ReturnOrder> list, String orderId){
        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(list.get(mid).getId().compareTo(orderId) > 0) hi = mid-1;
            else if(list.get(mid).getId().compareTo(orderId) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }

    public static ReturnOrder lookupReturnOrderByTrackingNumber(List<ReturnOrder> list, String trackingNumber){
        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(list.get(mid).getTrackingNumber().compareTo(trackingNumber) > 0) hi = mid-1;
            else if(list.get(mid).getTrackingNumber().compareTo(trackingNumber) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }

    public static Meas lookupMeasBySku(List<Meas> list, String sku){
        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(list.get(mid).getRelativeId().compareTo(sku) > 0) hi = mid-1;
            else if(list.get(mid).getRelativeId().compareTo(sku) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }

    public static ProductStock lookupProductStock(String id, List<ProductStock> stockList) {
        if(id == null) return null;
    
        int mid = 0;
        int lo = 0;
        int hi = stockList.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(stockList.get(mid).getId().compareTo(id) > 0) hi = mid-1;
            else if(stockList.get(mid).getId().compareTo(id) < 0) lo = mid+1;
            else {
                return stockList.get(mid);
            }
        }
        return null;
    }
}
