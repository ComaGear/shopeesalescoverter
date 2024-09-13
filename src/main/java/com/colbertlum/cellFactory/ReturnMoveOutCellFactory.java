package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import com.colbertlum.entity.ReturnMoveOut;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
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

            private TextField orderIdText = new TextField();

            private Text productDescriptionText = new Text();
            private Text quantityText = new Text();
            private Text returnStatus = new Text();
            private TextField returnQuantityTextFileField = new TextField();

            {
                orderIdText.setEditable(false);
                orderIdText.getStyleClass().add("copiable-text");
            }

            @Override
            public void updateItem(ReturnMoveOut returnMoveOut, boolean empty){
                
                if(returnMoveOut == null) return;
                
                orderIdText.setText(returnMoveOut.getOrderId());
                productDescriptionText.setText(returnMoveOut.getProductName() + " : " + returnMoveOut.getVariationName());
                quantityText.setText(formater.format(returnMoveOut.getQuantity()));
                returnStatus.setText(returnMoveOut.getReturnStatus());
                returnQuantityTextFileField.setText(formater.format(returnMoveOut.getStatusQuantity()));

                setGraphic(new HBox(orderIdText, productDescriptionText, quantityText, returnStatus, returnQuantityTextFileField));
            }
        };
    }

    public int getSelectedItemIndex() {
        return selectedIndex;
    }
    
}
