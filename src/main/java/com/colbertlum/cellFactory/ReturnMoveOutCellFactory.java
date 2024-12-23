package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.colbertlum.entity.ReturnMoveOut;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ReturnMoveOutCellFactory implements Callback<ListView<ReturnMoveOut>, ListCell<ReturnMoveOut>> {

    NumberFormat formater = new DecimalFormat("#0.00");

    @Override
    public ListCell<ReturnMoveOut> call(ListView<ReturnMoveOut> param) {
        return new ListCell<>() {

            private Text productDescriptionText = new Text();
            private Text skuText = new Text();
            private Text quantityText = new Text();
            private TextField returnQuantityTextFileField = new TextField();
            private ReturnMoveOut iReturnMoveOut;

            private MenuButton returnStatus = new MenuButton();
            private MenuItem returningMenuItem = new MenuItem(ReturnMoveOut.RETURNING);
            private MenuItem damagedMenuItem = new MenuItem(ReturnMoveOut.DAMAGED);
            private MenuItem lostMenuItem = new MenuItem(ReturnMoveOut.LOST);
            private MenuItem particularReceivedMenuItem = new MenuItem(ReturnMoveOut.PARTICULAR_RECEIVED);
            private MenuItem receivedMenuItem = new MenuItem(ReturnMoveOut.RECEIVED);
            private MenuItem noneMenuItem = new MenuItem(ReturnMoveOut.NONE);


            {
                returnStatus.getItems().add(returningMenuItem);
                returningMenuItem.setOnAction((e) ->{
                    if(iReturnMoveOut == null) return;
                    iReturnMoveOut.setReturnStatus(ReturnMoveOut.RETURNING);
                });
                returnStatus.getItems().add(damagedMenuItem);
                damagedMenuItem.setOnAction((e) ->{
                    if(iReturnMoveOut == null) return;
                    iReturnMoveOut.setReturnStatus(ReturnMoveOut.DAMAGED);
                });
                returnStatus.getItems().add(lostMenuItem);
                lostMenuItem.setOnAction((e) ->{
                    if(iReturnMoveOut == null) return;
                    iReturnMoveOut.setReturnStatus(ReturnMoveOut.LOST);
                });
                returnStatus.getItems().add(particularReceivedMenuItem);
                particularReceivedMenuItem.setOnAction((e) ->{
                    if(iReturnMoveOut == null) return;
                    iReturnMoveOut.setReturnStatus(ReturnMoveOut.PARTICULAR_RECEIVED);
                });
                returnStatus.getItems().add(receivedMenuItem);
                receivedMenuItem.setOnAction((e) ->{
                    if(iReturnMoveOut == null) return;
                    iReturnMoveOut.setReturnStatus(ReturnMoveOut.RECEIVED);
                });
                returnStatus.getItems().add(noneMenuItem);
                noneMenuItem.setOnAction((e) ->{
                    if(iReturnMoveOut == null) return;
                    iReturnMoveOut.setReturnStatus(ReturnMoveOut.NONE);
                });
            }

            @Override
            public void updateItem(ReturnMoveOut returnMoveOut, boolean empty){
                
                if(returnMoveOut == null) {
                    setText("");
                };
                
                iReturnMoveOut = returnMoveOut;

                productDescriptionText.setText(returnMoveOut.getProductName() + " : " + returnMoveOut.getVariationName());
                skuText.setText(returnMoveOut.getSku());
                quantityText.setText(formater.format(returnMoveOut.getQuantity()));
                returnStatus.setText(returnMoveOut.getReturnStatus());
                
                returnQuantityTextFileField.setText(formater.format(returnMoveOut.getStatusQuantity()));
                returnQuantityTextFileField.setPromptText("return");
                returnQuantityTextFileField.setPrefWidth(50);
                returnQuantityTextFileField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(!newValue.matches("\\d*")){
                        returnQuantityTextFileField.setText(newValue.replaceAll("[^\\d]", ""));
                    }

                    if(newValue != null && !newValue.isEmpty() && Integer.parseInt(newValue) >= 0) {
                        iReturnMoveOut.setStatusQuantity(Integer.parseInt(newValue));
                    }
                });
                    
                setGraphic(new HBox(productDescriptionText, quantityText, returnStatus, returnQuantityTextFileField));
            }
        };
    }
}
