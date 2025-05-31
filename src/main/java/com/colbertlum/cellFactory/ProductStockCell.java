package com.colbertlum.cellFactory;

import java.io.IOException;

import com.colbertlum.entity.ProductStock;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class ProductStockCell extends ListCell<ProductStock>{

    private static final String fxmlFile = "listCells/ProductStockListCell.fxml";

    private HBox content;
    private ProductStockStockCellController controller;

    @Override
    public void updateItem(ProductStock productStock, boolean empty){
        super.updateItem(productStock, empty);
        if(empty || productStock == null) {
            setGraphic(null);
        } else {
            controller.setData(productStock);
            setGraphic(content);
        }
    }

    public ProductStockCell(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlFile));
            content = loader.load();
            controller = loader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
