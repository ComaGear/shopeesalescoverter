package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.colbertlum.entity.ReturnMoveOut;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ReturnMoveOutCellFactory implements Callback<ListView<ReturnMoveOut>, ListCell<ReturnMoveOut>> {

    private int selectedIndex;


    NumberFormat formater = new DecimalFormat("#0.00");

    @Override
    public ListCell<ReturnMoveOut> call(ListView<ReturnMoveOut> param) {
        return new ListCell<>() {

            private Text productDescriptionText = new Text();
            private Text skuText = new Text();
            private Text quantityText = new Text();
            private MenuButton returnStatus = new MenuButton();
            private TextField returnQuantityTextFileField = new TextField();

            {

            }

            @Override
            public void updateItem(ReturnMoveOut returnMoveOut, boolean empty){
                
                if(returnMoveOut == null) return;
                
                productDescriptionText.setText(returnMoveOut.getProductName() + " : " + returnMoveOut.getVariationName());
                skuText.setText(returnMoveOut.getSku());
                quantityText.setText(formater.format(returnMoveOut.getQuantity()));
                returnStatus.setText(returnMoveOut.getReturnStatus());
                returnQuantityTextFileField.setText(formater.format(returnMoveOut.getStatusQuantity()));
                returnQuantityTextFileField.textProperty().addListener((observable, oldValue, newValue) -> {

                });

                setGraphic(new HBox(productDescriptionText, quantityText, returnStatus, returnQuantityTextFileField));
            }
        };
    }

    public int getSelectedItemIndex() {
        return selectedIndex;
    }
    
}
