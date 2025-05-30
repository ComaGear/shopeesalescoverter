package com.colbertlum.cellFactory;

import com.colbertlum.entity.ProductStock;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ProductStockStockCellController {
    
    @FXML
    TextField productId;
    @FXML
    Label productName;
    @FXML
    Label stock;
    @FXML
    Label allocatedStock;
    @FXML
    Label availableStock;

    public void setData(ProductStock productStock) {
        productId.setText(productStock.getId());
        productName.setText(productStock.getProductName());
        stock.setText(Double.toString(productStock.getStock()));
        allocatedStock.setText(Double.toString(productStock.getAllocatedStock()));
        availableStock.setText(Double.toString(productStock.getAvailableStock()));
    }
}
