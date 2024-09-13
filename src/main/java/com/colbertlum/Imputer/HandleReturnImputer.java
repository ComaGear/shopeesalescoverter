package com.colbertlum.Imputer;

import java.util.Comparator;
import java.util.List;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.ReturnOrder;

public class HandleReturnImputer {

    List<ReturnOrder> returnOrderList;

    private Comparator<ReturnOrder> trackingNumberComparator =  new Comparator<ReturnOrder>() {

        @Override
        public int compare(ReturnOrder o1, ReturnOrder o2) {
            return o1.getTrackingNumber().compareTo(o2.getTrackingNumber());
        }
        
    };

    private Comparator<ReturnOrder> orderIdComparator =  new Comparator<ReturnOrder>() {

        @Override
        public int compare(ReturnOrder o1, ReturnOrder o2) {
            return o1.getTrackingNumber().compareTo(o2.getTrackingNumber());
        }
        
    };
    
    public List<ReturnOrder> getReturnOrderList() {
        return returnOrderList;
    }

    public ReturnOrder getOrder(String text) {
        if(text == null || text.isEmpty() || text.equals("")) return null;
        
        ReturnOrder order = null;

        returnOrderList.sort(orderIdComparator);
        order = Lookup.lookupReturnOrder(returnOrderList, text);
        if(order != null) return order;

        returnOrderList.sort(trackingNumberComparator);
        order = Lookup.lookupReturnOrderByTrackingNumber(returnOrderList, text);
        if(order != null) return order;
    }
}
