package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.colbertlum.entity.ReturnMoveOut;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ReturnMoveOutCellFactory implements Callback<ListView<ReturnMoveOut>, ListCell<ReturnMoveOut>> {

    NumberFormat formater = new DecimalFormat("#");

    private ReturnMoveOut selectedReturnMoveOut = null;
    private boolean selecting = false;

    @Override
    public ListCell<ReturnMoveOut> call(ListView<ReturnMoveOut> param) {
        return new ListCell<>() {

            private final CheckBox checkBox = new CheckBox();

            private final Text productDescriptionText = new Text();
            private final Text skuText = new Text();
            private final Text quantityText = new Text();
            private final TextField returnQuantityTextFileField = new TextField();
            private final HBox hBox;

            private ReturnMoveOut iReturnMoveOut;
            
            private MenuButton returnStatus = new MenuButton();
            private MenuItem returningMenuItem = new MenuItem(ReturnMoveOut.RETURNING);
            private MenuItem damagedMenuItem = new MenuItem(ReturnMoveOut.DAMAGED);
            private MenuItem lostMenuItem = new MenuItem(ReturnMoveOut.LOST);
            private MenuItem particularReceivedMenuItem = new MenuItem(ReturnMoveOut.PARTICULAR_RECEIVED);
            private MenuItem receivedMenuItem = new MenuItem(ReturnMoveOut.RECEIVED);
            private MenuItem noneMenuItem = new MenuItem(ReturnMoveOut.NONE);


            {
                productDescriptionText.setWrappingWidth(400);
                skuText.setWrappingWidth(120);
                quantityText.setWrappingWidth(50);
                returnStatus.setPrefWidth(120);
                returnQuantityTextFileField.setPrefWidth(50);

                returnStatus.getItems().addAll(returningMenuItem, damagedMenuItem, lostMenuItem, particularReceivedMenuItem, receivedMenuItem, noneMenuItem);

                returnStatus.getItems().forEach((item) -> item.setOnAction(event ->{
                    if(iReturnMoveOut != null) {
                        iReturnMoveOut.setReturnStatus(item.getText());
                        returnStatus.setText(item.getText());
                    }
                }));

                checkBox.setOnAction((e)-> {
                    if(checkBox.isSelected()) {
                        selectedReturnMoveOut = iReturnMoveOut;
                    } else {
                        selectedReturnMoveOut = null;
                    }
                });

                hBox = new HBox(checkBox, productDescriptionText, skuText, quantityText, returnStatus, returnQuantityTextFileField);
                hBox.getChildren().forEach((child) -> HBox.setMargin(child, new Insets(2)));
            }

            @Override
            public void updateItem(ReturnMoveOut returnMoveOut, boolean empty){

                super.updateItem(returnMoveOut, empty);
                
                if(returnMoveOut == null) {
                    setText("");
                    return;
                };

                if(selecting) 
                    checkBox.setDisable(false);
                else checkBox.setDisable(true);
                
                iReturnMoveOut = returnMoveOut;

                productDescriptionText.setText(returnMoveOut.getName());
                skuText.setText(returnMoveOut.getSku());
                if(returnMoveOut.getQuantity() == 0) quantityText.setText("");
                else quantityText.setText(formater.format(returnMoveOut.getQuantity()));
                returnStatus.setText(returnMoveOut.getReturnStatus());
                
                returnQuantityTextFileField.setText(formater.format(returnMoveOut.getStatusQuantity()));
                returnQuantityTextFileField.setPromptText("");
                returnQuantityTextFileField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(!newValue.matches("\\d*")){
                        returnQuantityTextFileField.setText(newValue.replaceAll("[^\\d]", ""));
                    }

                    if(newValue != null && !newValue.isEmpty() && Integer.parseInt(newValue) >= 0) {
                        iReturnMoveOut.setStatusQuantity(Integer.parseInt(newValue));
                    }
                });

                checkBox.setDisable(!selecting);
                
                setGraphic(hBox);
            }
        };
    }

    public ReturnMoveOut getSelected(){
        return selectedReturnMoveOut;
    }

    public void setSelecting(boolean returnMovementSelecting) {
        this.selecting = returnMovementSelecting;
        if(returnMovementSelecting != true) selectedReturnMoveOut = null;
    }
}
