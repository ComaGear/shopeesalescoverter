package com.colbertlum.Controller;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.colbertlum.CustomListener;
import com.colbertlum.Imputer.HandleReturnImputer;
import com.colbertlum.cellFactory.ReturnMoveOutCellFactory;
import com.colbertlum.cellFactory.ReturnOrderCellFactory;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class HandleReturnController {
    
    private static final String FILTED_BY_ALL = "All";
    private static final String FILTED_BY_RETURN_REFUND = "Return/Refund";
    private static final String INCOMPLETED_RETURN = "Incompleted Return";
    private Stage stage;
    private Stack<Scene> sceneStack; 

    private HandleReturnImputer imputer;
    private ObservableList<ReturnOrder> observableReturnOrderList;
    private ListView<ReturnOrder> returnOrderListView;
    private String filterMode;
    private ListView<ReturnMoveOut> returnMovementListView;

    private Scene generatePanel(){

        TextField scaningSearchBar = new TextField();

        MenuButton filterOrderMenuButton = new MenuButton("filter by All");
        filterOrderMenuButton.setPrefWidth(100);
        MenuItem filterByAllItem = new MenuItem(FILTED_BY_ALL);
        MenuItem filteringRequestReturnRefundMenuItem = new MenuItem(FILTED_BY_RETURN_REFUND);
        MenuItem filteringByIncompletedMenuItem = new MenuItem(INCOMPLETED_RETURN);
        filterOrderMenuButton.getItems().add(filterByAllItem);
        filterOrderMenuButton.getItems().add(filteringRequestReturnRefundMenuItem);
        filterOrderMenuButton.getItems().add(filteringByIncompletedMenuItem);
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
        filteringByIncompletedMenuItem.setOnAction((e) ->{
            filterOrderMenuButton.setText("filter by Incompleted Return");
            filterMode = INCOMPLETED_RETURN;
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

        Scene mainScene = new Scene(new VBox(headerPanel, returnOrderListView));

        return mainScene;
    }

    private void refillOrderListView(List<ReturnOrder> returnOrders){
        if(observableReturnOrderList == null) {
            observableReturnOrderList = FXCollections.observableArrayList();
            returnOrderListView.setItems(observableReturnOrderList);
        }

        ArrayList<ReturnOrder> filtedOrders = new ArrayList<ReturnOrder>();
        
        switch(filterMode){
            case FILTED_BY_ALL:
                filtedOrders.addAll(returnOrders);
                break;
            case FILTED_BY_RETURN_REFUND:
                for(ReturnOrder order : returnOrders){
                    if(order.getReturnType().equals(FILTED_BY_RETURN_REFUND)){
                        filtedOrders.add(order);
                    }
                }
                break;
            case INCOMPLETED_RETURN:
                for(ReturnOrder order : returnOrders){
                    if(!order.getReturnMoveOutList().get(0).get().getReturnStatus().equals(ReturnMoveOut.RETURNING)){
                        filtedOrders.add(order);
                    }
                }
                break;
        }

        observableReturnOrderList.clear();
        observableReturnOrderList.addAll(filtedOrders);
    }


    private void openReturnMovementHandleScene(ReturnOrder returnOrder){

        
        TextField orderIdText = new TextField();
        orderIdText.setEditable(false);
        orderIdText.getStyleClass().add("copiable-text");
        orderIdText.setText(returnOrder.getId());

        TextField scaningSearchBar = new TextField();

        scaningSearchBar.setOnKeyPressed((keyEvent) ->{
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                ReturnMoveOut returnMoveOut = imputer.getReturnMoveOuInOrder(returnOrder, scaningSearchBar.getText());
                if(returnMoveOut == null) scaningSearchBar.selectAll();
            }
        });
        scaningSearchBar.textProperty().addListener((observe, oldValue, newValue) -> {
            ArrayList<ReturnMoveOut> newReturnMoveOutList = new ArrayList<ReturnMoveOut>();

            List<SoftReference<ReturnMoveOut>> returnMoveOutList = returnOrder.getReturnMoveOutList();
            for(SoftReference<ReturnMoveOut> softReturnMoveOut : returnMoveOutList){
                if(softReturnMoveOut.get().getProductName().contains(newValue)) {
                    newReturnMoveOutList.add(softReturnMoveOut.get());
                } else if(softReturnMoveOut.get().getVariationName().contains(newValue)){
                    newReturnMoveOutList.add(softReturnMoveOut.get()); 
                }
            }
            refillMovementListView(newReturnMoveOutList);
        });
        scaningSearchBar.focusedProperty().addListener((observe, oldValue, newValue) ->{
            Platform.runLater(() ->{
                if(scaningSearchBar.isFocused() && !scaningSearchBar.getText().isEmpty()) scaningSearchBar.selectAll();
            });
        });

        Button backButton = new Button("back to Orders");
        backButton.setOnAction((e) -> {
            popScene();
            refillMovementListView(new ArrayList<>());
        });

        Pane spacer = new Pane();
        spacer.setMinSize(10, 1);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerPanel = new HBox(orderIdText, scaningSearchBar, spacer, backButton);


        returnMovementListView = new ListView<ReturnMoveOut>();
        ReturnMoveOutCellFactory returnMovemCellFactory = new ReturnMoveOutCellFactory();
        returnMovementListView.setCellFactory(returnMovemCellFactory);
        // ReturnMoveOut returnMoveOut = listView.getItems().get(cellFactory.getSelectedItemIndex());

        Scene subScene = new Scene(new VBox(headerPanel, returnMovementListView));
        subScene.getStylesheets().add(getClass().getResource("copiable-text.css").toExternalForm());
        pushScene(subScene);
    }

    private void refillMovementListView(List<ReturnMoveOut> returnMoveOuts){
        returnMovementListView.getItems().clear();

        returnMovementListView.getItems().addAll(returnMoveOuts);
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

        stage.setScene(generatePanel());
        
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
