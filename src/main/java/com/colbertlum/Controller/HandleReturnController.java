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
    
    private Stage stage;
    private Scene mainScene;
    private Scene subScene;
    private Stack<Scene> sceneStack; 


    private HandleReturnImputer imputer;
    private ObservableList<ReturnOrder> observableArrayList;
    private ListView<ReturnOrder> returnOrderListView;

    private Scene generatePanel(){


        MenuButton filterOrderMenuButton = new MenuButton("filter by All");
        MenuItem filterByAllItem = new MenuItem("All");
        MenuItem filteringRequestReturnRefundMenuItem = new MenuItem("Request Return/Refund");
        filterOrderMenuButton.getItems().addAll(filterByAllItem);


        TextField scaningSearchBar = new TextField();
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

    private void refillOrderListView(List<ReturnOrder> ReturnOrders){
        if(observableArrayList == null) {
            observableArrayList = FXCollections.observableArrayList();
            returnOrderListView.setItems(observableArrayList);
        }
        observableArrayList.clear();
        observableArrayList.addAll(ReturnOrders);
    }


    private void openReturnMovementHandleScene(ReturnOrder order){


        ListView<ReturnMoveOut> returnMovementListView = new ListView<ReturnMoveOut>();
        ReturnMoveOutCellFactory returnMovemCellFactory = new ReturnMoveOutCellFactory();
        returnMovementListView.setCellFactory(returnMovemCellFactory);
        // ReturnMoveOut returnMoveOut = listView.getItems().get(cellFactory.getSelectedItemIndex());

        subScene = new Scene();
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
