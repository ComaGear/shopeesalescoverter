package com.colbertlum.Imputer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.colbertlum.OrderRepository;
import com.colbertlum.OrderService;
import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.Controller.StockReportInspectorController;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.contentHandler.StockReportContentFactory;
import com.colbertlum.entity.ProductStock;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StockReportInspectorImputer {

    public void initStageAndShow(Stage priStage, List<ProductStock> list) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(StockReportInspectorController.fxmlFile));
        fxmlLoader.setLocation(getClass().getClassLoader().getResource(StockReportInspectorController.fxmlFile));
        Stage stage = new Stage();
        VBox vBox = null;
        try {
            vBox = fxmlLoader.load();
            stage.setScene(new Scene(vBox));
            StockReportInspectorController controller = fxmlLoader.getController();
            controller.setProductStockList(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setTitle("Inspect Stocks");
        stage.setWidth(vBox.getWidth());
        stage.setHeight(vBox.getHeight());
        double centerX = stage.getX() + (vBox.getWidth() / 2);
        double centerY = stage.getY() + (vBox.getHeight() / 2);
        stage.setX(centerX - (stage.getWidth() / 2));
        stage.setY(centerY - (stage.getHeight() / 2));

        stage.showAndWait();

    }

    private void addAllocatedStock(List<ProductStock> productStocks, Map<String, Double> allocatedMap){
        productStocks.sort((o1, o2) -> {
            return o1.getId().compareTo(o2.getId());
        });

        for(String key : allocatedMap.keySet()) {
            ProductStock lookupProductStock = Lookup.lookupProductStock(key, productStocks);
            if(lookupProductStock == null) continue;
            lookupProductStock.setAllocatedStock(allocatedMap.get(key));
            lookupProductStock.setAvailableStock(lookupProductStock.getStock() - lookupProductStock.getAllocatedStock());
        }
    }

    public List<ProductStock> loadProductStocks() {
        List<ProductStock> stockReport = null;

        try {
            stockReport = StockReportContentFactory.getStockReport();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(stockReport == null) return null;

        for(ProductStock stock : stockReport) {
            stock.setAvailableStock(stock.getStock());
        }

        if(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE) != null 
            && ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.IS_RESERVING_MANUAL_RESERVING_STOCK).equals("true")){
            Map<String, Double> manualReservingStock = StockReportContentFactory.getManualReservingStock();
            addAllocatedStock(stockReport, manualReservingStock);
        }
            
        OrderService orderService = new OrderService(new OrderRepository(true));
        addAllocatedStock(stockReport, orderService.getOnShippingStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getReservedDamagedStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getReservedInReturningStockQuantity());
        // orderService.reduceStockMap(stockReport, orderService.getOnShippingStockQuantity());

        return stockReport;
    }

    public StockReportInspectorImputer() {
        
    }
}
