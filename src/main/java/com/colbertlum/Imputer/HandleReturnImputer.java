package com.colbertlum.Imputer;

import java.lang.ref.SoftReference;
import java.util.Comparator;
import java.util.List;

import com.colbertlum.OrderRepository;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

public class HandleReturnImputer {

    List<ReturnOrder> returnOrderList;
    List<ReturnMoveOut> returnMoveOutList;
    List<Meas> measList = ShopeeSalesConvertApplication.getMeasList();
    
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
    private OrderRepository orderRepository;
    
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
        return null;
    }

    public ReturnMoveOut getReturnMoveOuInOrder(ReturnOrder order, String text) {
        for(SoftReference<ReturnMoveOut> softMoveOut :  order.getReturnMoveOutList()){

            String sku = softMoveOut.get().getSku();

            if(sku.equals(text)) return softMoveOut.get();

            measList.sort((o1, o2) -> o1.getRelativeId().compareTo(o2.getRelativeId()));
            Meas meas = Lookup.lookupMeasBySku(measList, sku);
            if(meas != null && meas.getId().equals(text)) return softMoveOut.get();
        }
        return null;
    }

    public void saveTransaction() {
        orderRepository.submitTransaction();
    }

    public HandleReturnImputer(){
        orderRepository = new OrderRepository();
        this.returnOrderList = orderRepository.getReturnOrders();
        this.returnMoveOutList = orderRepository.getReturnMoveOuts();
    }
}
