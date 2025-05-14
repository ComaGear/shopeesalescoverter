package com.colbertlum.Imputer;

import java.util.Map;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.contentHandler.StockReportContentFactory;

public class StockReportInspector {
    public void loading() {
        if(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE) != null && ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE).equals("true")){
                Map<String, Double> manualReservingStock = StockReportContentFactory.getManualReservingStock();
                // orderService.reduceStockMap(stockReport, manualReservingStock);
            }

        OrderService orderService = new OrderService(new OrderRepository(true));
        // orderService.reduceStockMap(stockReport, orderService.getReservedDamagedStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getReservedInReturningStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getOnShippingStockQuantity());
    }
}
