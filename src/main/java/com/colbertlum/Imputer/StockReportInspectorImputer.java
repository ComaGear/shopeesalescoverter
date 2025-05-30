package com.colbertlum.Imputer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Controller.StockReportInspectorController;
import com.colbertlum.contentHandler.StockReportContentFactory;
import com.colbertlum.entity.ProductStock;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockReportInspectorImputer {

    private void initStage(Stage priStage, List<ProductStock> list) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(StockReportInspectorController.fxmlFile));
        VBox vBox = null;
        try {
            vBox = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(vBox));
            StockReportInspectorController controller = fxmlLoader.getController();
            controller.setProductStockList(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addAllocatedStock(List<ProductStock> productStocks, Map<String, Double> allocatedMap){
        
    }

    public StockReportInspectorImputer() {
        List<ProductStock> stockReport = null;

        try {
            stockReport = StockReportContentFactory.getStockReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(stockReport == null) return;


        if(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE) != null && ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE).equals("true")){
            Map<String, Double> manualReservingStock = StockReportContentFactory.getManualReservingStock();
            addAllocatedStock(stockReport, manualReservingStock);
        }
            
        OrderService orderService = new OrderService(new OrderRepository(true));
        addAllocatedStock(stockReport, orderService.getOnShippingStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getReservedDamagedStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getReservedInReturningStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getOnShippingStockQuantity());


    }
}
