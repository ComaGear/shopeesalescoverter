package com.colbertlum.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.colbertlum.CustomListener;
import com.colbertlum.Imputer.HandleReturnImputer;
import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.cellFactory.ReturnMoveOutCellFactory;
import com.colbertlum.cellFactory.ReturnOrderCellFactory;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HandleReturnController {
    
    private static final String FILTED_BY_ALL = "All";
    private static final String FILTED_BY_RETURN_REFUND = "Return/Refund";
    private Stage stage;
    private Scene mainScene;
    private Scene subScene;
    private Stack<Scene> sceneStack; 


    private HandleReturnImputer imputer;
    private ObservableList<ReturnOrder> observableArrayList;
    private ListView<ReturnOrder> returnOrderListView;
    private String filterMode;

    private Scene generatePanel(){

        TextField scaningSearchBar = new TextField();

        MenuButton filterOrderMenuButton = new MenuButton("filter by All");
        filterOrderMenuButton.setPrefWidth(100);
        MenuItem filterByAllItem = new MenuItem(FILTED_BY_ALL);
        MenuItem filteringRequestReturnRefundMenuItem = new MenuItem("Request Return/Refund");
        filterOrderMenuButton.getItems().addAll(filterByAllItem);
        filterOrderMenuButton.getItems().addAll(filteringRequestReturnRefundMenuItem);
        filterByAllItem.setOnAction((e) -> {
            filterOrderMenuButton.setText("filter by All");
            filterMode = FILTED_BY_ALL;
            scaningSearchBar.setText("");
            refillOrderListView(imputer.getReturnOrderList());
        });
        filteringRequestReturnRefundMenuItem.setOnAction((e) -> {
            filterOrderMenuButton.setText("filter by Request Return/Refund");
            filterMode = FILTED_BY_RETURN_REFUND;
            scaningSearchBar.setText("");
            refillOrderListView(imputer.getReturnOrderList());
        });


        // scan tracking number or Order id, if order is exist will open return movement page directly,
        // if order not is exist or not found, select all text let user can replace old text with new text.
        // scaningSearchBar.textProperty().addListener((observe, oldText, newText) ->{
        //     imputer
        // });
        scaningSearchBar.setOnKeyPressed((keyEvent) ->{
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                ReturnOrder order = imputer.getOrder(scaningSearchBar.getText());
                if(order != null) openReturnMovementHandleScene(order);
                if(order == null) scaningSearchBar.selectAll();
            }
        });
        scaningSearchBar.textProperty().addListener((observe, oldValue, newValue) -> {
            ArrayList<ReturnOrder> orders = new ArrayList<ReturnOrder>();
            for(ReturnOrder order : imputer.getReturnOrderList()){
                if(order.getId().toLowerCase().contains(newValue.toLowerCase())) orders.add(order);
                if(order.getTrackingNumber().toLowerCase().contains(newValue.toLowerCase())) orders.add(order);
            }
            refillOrderListView(orders);
        });
        scaningSearchBar.focusedProperty().addListener((observe, oldValue, newValue) ->{
            Platform.runLater(() ->{
                if(scaningSearchBar.isFocused() && !scaningSearchBar.getText().isEmpty()) scaningSearchBar.selectAll();
            });
        });

        HBox headerPanel = new HBox(filterOrderMenuButton, scaningSearchBar);

        returnOrderListView = new ListView<ReturnOrder>();
        ReturnOrderCellFactory returnOrderCellFactory = new ReturnOrderCellFactory(new CustomListener() {
            @Override
            public void handleOrder(String orderId){
                ReturnOrder order = imputer.getOrder(orderId);
                if(order != null) openReturnMovementHandleScene(order);
            }
        });
        returnOrderListView.setCellFactory(returnOrderCellFactory);
        refillOrderListView(imputer.getReturnOrderList());

        mainScene = new Scene(new VBox(headerPanel, returnOrderListView));

        return mainScene;
    }

    private void refillOrderListView(List<ReturnOrder> returnOrders){
        if(observableArrayList == null) {
            observableArrayList = FXCollections.observableArrayList();
            returnOrderListView.setItems(observableArrayList);
        }

        switch(filterMode){
            case FILTED_BY_ALL:
                observableArrayList.clear();
                observableArrayList.addAll(returnOrders);
                break;
            case FILTED_BY_RETURN_REFUND:
                ArrayList<ReturnOrder> filtedOrders = new ArrayList<ReturnOrder>();
                for(ReturnOrder order : returnOrders){
                    if(order.getReturnType().equals(FILTED_BY_RETURN_REFUND)){
                        filtedOrders.add(order);
                    }
                }

                observableArrayList.clear();
                observableArrayList.addAll(returnOrders);
                break;
        }
    }


    private void openReturnMovementHandleScene(ReturnOrder order){


        ListView<ReturnMoveOut> returnMovementListView = new ListView<ReturnMoveOut>();
        ReturnMoveOutCellFactory returnMovemCellFactory = new ReturnMoveOutCellFactory();
        returnMovementListView.setCellFactory(returnMovemCellFactory);
        // ReturnMoveOut returnMoveOut = listView.getItems().get(cellFactory.getSelectedItemIndex());

        subScene = new Scene(new VBox());
        subScene.getStylesheets().add(getClass().getResource("copiable-text.css").toExternalForm());
        pushScene(subScene);
    }

    private ListView<ReturnMoveOut> refillMovementListView(ListView<ReturnMoveOut> listView, List<ReturnMoveOut> returnMoveOuts){
        listView.getItems().clear();

        listView.getItems().addAll(returnMoveOuts);
        return listView;
    }

    public void initDialog(Stage stage){
        this.stage = stage;

        stage.setTitle("Handle returning order");
        stage.setWidth(1400);
        stage.setHeight(600);
        stage.centerOnScreen();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {

                imputer.saveTransaction();
            }
            
        });
        
    }

    public Stage getStage() {
        return stage;
    }

    private void pushScene(Scene scene) {
        if(sceneStack == null) sceneStack = new Stack<Scene>();

        sceneStack.push(scene);
        stage.setScene(sceneStack.peek());
    }

    private void popScene(){
        sceneStack.pop();
        stage.setScene(sceneStack.peek());
    }


    public HandleReturnController(){
        this.imputer = new HandleReturnImputer();
    }
}
