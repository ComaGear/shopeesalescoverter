package com.colbertlum.Imputer.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ProductStock;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

public class Lookup {

    public static Order lookupOrder(List<Order> list, String orderId){

        // int index = Collections.binarySearch(list, new Order(orderId), Comparator.comparing(Order::getId));
        // return index >= 0 ? list.get(index) : null;

        String o2lo = orderId.toLowerCase();
        o2lo.trim();

        int mid = 0;
        int lo = 0;
        int hi = list.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;

            String o1lo = list.get(mid).getId().toLowerCase();
            o1lo.trim();


            if(o1lo.compareTo(o2lo) > 0) hi = mid-1;
            else if(o1lo.compareTo(o2lo) < 0) lo = mid+1;
            else {
                return list.get(mid);
            }
        }
        return null;
    }

    public static Order dumpLookupOrder(List<Order> list, String orderId) {
        if(orderId == null || orderId.isEmpty()) return null;
        orderId = orderId.trim();
        list.sort((o1, o2) -> o1.getOrderCreationDate().compareTo(o2.getOrderCreationDate()));

        for(Order listOrder : list) {
            String listOrderId = listOrder.getId().trim();
            if(listOrderId.equals(orderId)) return listOrder;
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

    public static ReturnMoveOut lookupReturnMoveOutByObjectAndSorting(List<ReturnMoveOut> returnMoveOuts, ReturnMoveOut returnMoveOut) {

        returnMoveOuts.sort((o1, o2) -> {
            String uqo1 = o1.getOrderId() + "-" + o1.getProductName() + "-" + o1.getVariationName() + "-" + o1.getReturnStatus() + "-" + o1.getStatusQuantity();
            String uqo2 = o2.getOrderId() + "-" + o2.getProductName() + "-" + o2.getVariationName() + "-" + o2.getReturnStatus() + "-" + o2.getStatusQuantity();
            return uqo1.compareTo(uqo2);
        });
        
        String uqo2 = returnMoveOut.getOrderId() + "-" + returnMoveOut.getProductName() + "-" + returnMoveOut.getVariationName() + "-" + returnMoveOut.getReturnStatus() + "-" + returnMoveOut.getStatusQuantity();

        int mid = 0;
        int lo = 0;
        int hi = returnMoveOuts.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            ReturnMoveOut o1 = returnMoveOuts.get(mid);
            String uqo1 = o1.getOrderId() + "-" + o1.getProductName() + "-" + o1.getVariationName() + "-" + o1.getReturnStatus() + "-" + o1.getStatusQuantity();
            if(uqo1.compareTo(uqo2) > 0) hi = mid-1;
            else if(uqo1.compareTo(uqo2) < 0) lo = mid+1;
            else {
                return returnMoveOuts.get(mid);
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
