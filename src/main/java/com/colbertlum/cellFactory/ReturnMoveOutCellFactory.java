package com.colbertlum.cellFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import com.colbertlum.entity.ReturnMoveOut;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ReturnMoveOutCellFactory implements Callback<ListView<ReturnMoveOut>, ListCell<ReturnMoveOut>> {

    List<ReturnMoveOut> selectReturnMoveOuts;
    List<ListCell<ReturnMoveOut>> selectedListCell;


    NumberFormat formater = new DecimalFormat("#0.00");

    @Override
    public ListCell<ReturnMoveOut> call(ListView<ReturnMoveOut> param) {
        return new ListCell<>() {

            private TextField orderIdText = new TextField("");

            Text productDescriptionText = new Text(returnMoveOut.getProductName() + " : " + returnMoveOut.getVariationName());
            Text quantityText = new Text(formater.format(returnMoveOut.getQuantity()));
            Text returnStatus = new Text(returnMoveOut.getReturnStatus());
            TextField returnQuantityTextFileField = new TextField(formater.format(returnMoveOut.getStatusQuantity()));
            
            {
                orderIdText.setEditable(false);
                orderIdText.getStyleClass().add("copiable-text");
            }

            @Override
            public void updateItem(ReturnMoveOut returnMoveOut, boolean empty){

                

                if(returnMoveOut == null) return;
                
                orderIdText.setText(returnMoveOut.getOrderId());

                setGraphic(new HBox(orderIdText, productDescriptionText, quantityText, returnStatus, returnQuantityTextFileField));
            }
        };
    }

    public updateSelectedListView(ReturnMoveOut returnMoveOut){
        
    }
    
}
