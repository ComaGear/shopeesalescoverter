package com.colbertlum.Imputer.Utils;

import java.util.List;

import com.colbertlum.entity.Order;
import com.colbertlum.entity.OrderStatusTracking;

public class Lookup {
    
    public static OrderStatusTracking lookupOrderStatusTracking(List<OrderStatusTracking> list, String orderId){
        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(list.get(mid).getOrderId().compareTo(orderId) > 0) hi = mid-1;
            else if(list.get(mid).getOrderId().compareTo(orderId) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }

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
}
