package com.colbertlum.cellFactory;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.colbertlum.HandleOpenOrderFormListener;
import com.colbertlum.OrderService;
import com.colbertlum.constants.OrderInternalStatus;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ReturnOrderCellFactory implements Callback<ListView<ReturnOrder>, ListCell<ReturnOrder>> {

    private HandleOpenOrderFormListener handler;

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public ListCell<ReturnOrder> call(ListView<ReturnOrder> param) {
        return new ListCell<>(){

            private TextField orderIdText = new TextField();

            private Text orderShipOutDate = new Text();
            private Text orderCompletedDate = new Text();
            private Text returnType = new Text();
            private Button openReturnButton = new Button("open movement");

            {
                orderIdText.setEditable(false);
                orderIdText.getStyleClass().add("copiable-text");

                orderIdText.setPrefWidth(150);
                orderShipOutDate.setWrappingWidth(100);
                orderCompletedDate.setWrappingWidth(100);
                returnType.setWrappingWidth(100);
            }

            @Override
            public void updateItem(ReturnOrder returnOrder, boolean empty){
                
                super.updateItem(returnOrder, empty);

                if(returnOrder == null || empty) {
                    setText("");
                    setGraphic(null);
                    return;
                }
                
                orderIdText.setText(returnOrder.getId());
                orderShipOutDate.setText(returnOrder.getShipOutDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                if(returnOrder.getOrderCompleteDate() != null) {
                    orderCompletedDate.setText(returnOrder.getOrderCompleteDate().format(DateTimeFormatter.ofPattern(DATE_PATTERN)));
                } else {
                    orderCompletedDate.setText("");
                }
                // if(returnOrder.getStatus().equals(OrderService.STATUS_COMPLETE) && returnOrder.isRequestApproved()){
                if(returnOrder.getInternalStatus().equals(OrderInternalStatus.AFTER_SALES_RETURN)){
                    returnType.setText("Return After Completed");
                } else {
                    returnType.setText("Failed Delivery");
                }
                openReturnButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        handler.handleOrder(returnOrder.getId());
                        event.consume();
                    }
                                
                });

                HBox hbox = new HBox(orderIdText, orderShipOutDate, orderCompletedDate, returnType, openReturnButton);
                hbox.getChildren().forEach((child) -> HBox.setMargin(child, new Insets(5)));
                setGraphic(hbox);
            }
        };
    }

    public ReturnOrderCellFactory(HandleOpenOrderFormListener handler){
        this.handler = handler;
    }

    public ReturnOrderCellFactory(){
    }
    
}
