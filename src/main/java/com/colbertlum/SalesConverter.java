package com.colbertlum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class SalesConverter {
    
    private List<MoveOut> moveOuts;
    private ArrayList<Meas> measList;
    private List<MoveOut> EmptySkuMoveOuts;
    private List<MoveOut> notExistSkuMoveOuts;
    private List<MoveOut> advanceFillMoveOuts;

    public List<MoveOut> getAdvanceFillMoveOuts() {
        return advanceFillMoveOuts;
    }

    public List<MoveOut> getNotExistSkuMoveOuts() {
        return notExistSkuMoveOuts;
    }

    public boolean hasNotExistSkuMoveOut(){
        if(notExistSkuMoveOuts == null) return false;
        return !notExistSkuMoveOuts.isEmpty();
    }

    public List<MoveOut> getEmptySkuMoveOuts() {
        return EmptySkuMoveOuts;
    }

    public boolean hasEmptySkuMoveOut(){
        if(EmptySkuMoveOuts == null) return false;
        return !EmptySkuMoveOuts.isEmpty();
    }

    public boolean hasAdvanceFillMoveOut(){
        if(advanceFillMoveOuts == null) return false;
        return !advanceFillMoveOuts.isEmpty();
    }

    public SalesConverter(List<MoveOut> moveOuts, ArrayList<Meas> measList){
        this.moveOuts = moveOuts;
        this.measList = measList;
    }   

    public List<MoveOut> process(){

        this.advanceFillMoveOuts = advanceFillEmptySku(moveOuts);
        this.EmptySkuMoveOuts = this.cleanEmptySku(this.moveOuts); // getting out emptry sku moveOut
        this.calculateActualPrice(this.moveOuts);
        this.convertMeas();
        return this.moveOuts;
    }

    private List<MoveOut> advanceFillEmptySku(List<MoveOut> moveOuts) {

        ArrayList<MoveOut> advanceFill = new ArrayList<MoveOut>();

        measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
                if(o1.getOnlineProductName().compareTo(o2.getOnlineProductName()) == 0){
                    return o1.getOnlineVariationName().compareTo(o2.getOnlineVariationName());
                } else {
                    return o1.getOnlineProductName().compareTo(o2.getOnlineProductName());
                }
            }
            
        });
        
        for(MoveOut moveOut : moveOuts){
            if(moveOut.getSku() == null || moveOut.getSku().isEmpty()){
                Meas meas = search(moveOut.getProductName(), moveOut.getVariationName(), measList);
                moveOut.setSku(meas.getRelativeId());
                advanceFill.add(moveOut);
            }
        }

        return advanceFill;
    }

    private void calculateActualPrice(List<MoveOut> moveOuts) {
        
        for(MoveOut moveOut : moveOuts){
            Order order = moveOut.getOrder();

            double subtotal = moveOut.getPrice() * moveOut.getQuantity(); 
            double totalFee = order.getManagementFee() + order.getTransactionFee() + order.getCommissionFee() + order.getServiceFee();
            double totalAmountReduceShippingFee = order.getOrderTotalAmount() + order.getShopeeVoucher() - order.getShippingFee() + order.getShippingRebateEstimate();

            double subFee = totalFee * (subtotal / totalAmountReduceShippingFee);
            double priceReduceFee = (subtotal - subFee) / moveOut.getQuantity();

            moveOut.setPrice(priceReduceFee);
        }
    }

    private void convertMeas() {

        this.measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
                return o1.getRelativeId().compareTo(o2.getRelativeId());
            }
        });

        this.moveOuts.sort(new Comparator<MoveOut>() {

            @Override
            public int compare(MoveOut o1, MoveOut o2) {
                return o1.getSku().compareTo(o2.getSku());
            }
        });

        for(MoveOut moveOut : moveOuts){
            Meas meas = binarySearch(moveOut, measList);
            if(meas == null) {
                if(notExistSkuMoveOuts == null ) this.notExistSkuMoveOuts = new ArrayList<MoveOut>();
                notExistSkuMoveOuts.add(moveOut);
                continue;
            }
            moveOut.setId(meas.getId());
            moveOut.setQuantity(meas.getMeasurement() * moveOut.getQuantity());
            moveOut.setPrice(moveOut.getPrice() / meas.getMeasurement());

            meas.setOnlineProductName(moveOut.getProductName());
            meas.setOnlineVariationName(moveOut.getVariationName());
        }
    }

    private Meas search(String productName, String variationName, List<Meas> measList){
        int lo = 0;
        int hi = measList.size()-1;

        while(lo <= hi) {
            int mid = lo + (hi-lo) / 2;
            if(measList.get(mid).getOnlineProductName().compareTo(productName) > 0) hi = mid-1; 
            else if(measList.get(mid).getOnlineProductName().compareTo(productName) < 0) lo = mid+1;
            else{
                if(measList.get(mid).getOnlineVariationName().compareTo(variationName) > 0) hi = mid-1; 
                else if(measList.get(mid).getOnlineVariationName().compareTo(variationName) < 0) lo = mid+1;
                else return measList.get(mid);
            }
        }
        return null;
    }
    
    private Meas binarySearch(MoveOut moveOut, List<Meas> measList){

        int lo = 0;
        int hi = measList.size()-1;

        while(lo <= hi) {
            int mid = lo + (hi-lo) / 2;
            if(measList.get(mid).getRelativeId().compareTo(moveOut.getSku()) > 0) hi = mid-1; 
            else if(measList.get(mid).getRelativeId().compareTo(moveOut.getSku()) < 0) lo = mid+1;
            else{
                return measList.get(mid);
            }
        }
        return null;
    }

    private List<MoveOut> cleanEmptySku(List<MoveOut> moveOuts){
        // moveOuts.sort(new Comparator<MoveOut>() {

        //     @Override
        //     public int compare(MoveOut o1, MoveOut o2) {
        //         if(o1.getSku() == null || o1.getSku().equals("")) return -1;
        //         if(o2.getSku() == null || o2.getSku().equals("")) return 1;
        //         return o1.getSku().compareTo(o2.getSku());
        //     }
            
        // });

        List<MoveOut> EmptySkuMoveOuts = new ArrayList<MoveOut>();  
        for(MoveOut moveOut : moveOuts){
            if(moveOut.getSku() == null || moveOut.getSku().isEmpty()){
                EmptySkuMoveOuts.add(moveOut);
            }
        }

        moveOuts.removeAll(EmptySkuMoveOuts);
        return EmptySkuMoveOuts;
    }
}
