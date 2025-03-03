package com.colbertlum.Imputer;

import java.util.Comparator;
import java.util.List;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.BigSellerStockCounting;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.ProductStock;

public class BigSellerStockCountingImputer {

    private MeasImputer measImputer;
    private OrderService orderService;
    private List<MoveOut> moveOuts;

    private 

    public void update(List<ProductStock> productStockList, List<BigSellerStockCounting> countingList) {

        productStockList.sort(getComparatorOfPrductStockList());
        orderService.reduceStockMap(productStockList, orderService.getReservedDamagedStockQuantity());
        orderService.reduceStockMap(productStockList, orderService.calculatePendingOrderStockRequirement(moveOuts));
        
        for(BigSellerStockCounting counting : countingList){
            String sku = counting.getSKU();
            if(sku == null) {
                counting.setStock(counting.getOnHand());
                // TODO : Suggest should generate a report which is empty sku.
            }
            Meas meas = measImputer.getMeas(sku, measImputer.getMeasList());
            if(meas == null) {
                counting.setStock(0);
            }
            (productStock * meas.getMeasurement()) * meas.getUpdateRule()
            ProductStock productStock = Lookup.lookupProductStock(meas.getId(), productStockList);
            
            counting.setStock(productStock.getAvailableStock());
            
        }
    }

    public BigSellerStockCountingImputer(List<MoveOut> moveOuts) {
        measImputer = new MeasImputer();
        orderService = new OrderService(new OrderRepository(true));

        // moveOuts only use for reduce pending order stock requirement
        this.moveOuts = moveOuts;
    }
    
    private Comparator<ProductStock> getComparatorOfPrductStockList() {
        return new Comparator<ProductStock>() {

            @Override
            public int compare(ProductStock o1, ProductStock o2) {
                return o1.getId().compareTo(o2.getId());
            }

            
        };
    }

    private Comparator
}
