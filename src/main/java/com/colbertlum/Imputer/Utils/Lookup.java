package com.colbertlum.Imputer.Utils;

import java.util.List;

import com.colbertlum.entity.OrderStatusTracking;

public class Lookup {
    
    public static OrderStatusTracking lookupOrderStatusTracking(List<OrderStatusTracking> list, String OrderId){
        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(list.get(mid).getOrderId().compareTo(OrderId) > 0) hi = mid-1;
            else if(list.get(mid).getOrderId().compareTo(OrderId) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }
}
