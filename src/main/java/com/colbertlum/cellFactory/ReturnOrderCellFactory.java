package com.colbertlum.cellFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.colbertlum.CustomListener;
import com.colbertlum.Controller.HandleReturnController;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ReturnOrderCellFactory implements Callback<ListView<ReturnOrder>, ListCell<ReturnOrder>> {

    private List<ReturnMoveOut> returnMoveOuts;
    private CustomListener handler;

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    @Override
    public ListCell<ReturnOrder> call(ListView<ReturnOrder> param) {
        return new ListCell<>(){

            private TextField orderIdText = new TextField();

            private Text orderShipOutDate = new Text();
            private Text orderCompletedDate = new Text();
            private Text returnType = new Text();
            private Button openReturnButton = new Button();

            {
                orderIdText.setEditable(false);
                orderIdText.getStyleClass().add("copiable-text");

            }

            @Override
            public void updateItem(ReturnOrder returnOrder, boolean empty){
                
                if(returnOrder == null) return;
                
                orderIdText.setText(returnOrder.getId());
                orderShipOutDate.setText(returnOrder.getShipOutDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                orderCompletedDate.setText(returnOrder.getOrderCompleteDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                if(returnOrder.getStatus().equals(Order.STATUS_COMPLETED) && returnOrder.isRequestApproved()){
                    returnType.setText("Return After Completed");
                } else {
                    returnType.setText("Failed Delivery");
                }
                openReturnButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        handler.handleOrder(returnOrder.getId());
                    }
                                
                });

                setGraphic(new HBox(orderIdText, orderShipOutDate, orderCompletedDate, returnType, openReturnButton));
            }
        };
    }

    public ReturnOrderCellFactory(CustomListener handler){
        this.handler = handler;
    }

    public ReturnOrderCellFactory(){
    }
    
}
