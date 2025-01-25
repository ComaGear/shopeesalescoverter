package com.colbertlum.Controller;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.colbertlum.HandleOpenOrderFormListener;
import com.colbertlum.Imputer.HandleReturnImputer;
import com.colbertlum.cellFactory.ReturnMoveOutCellFactory;
import com.colbertlum.cellFactory.ReturnOrderCellFactory;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnOrder;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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
    private ObservableList<ReturnMoveOut> observableReturnMoveOutList;
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
        ReturnOrderCellFactory returnOrderCellFactory = new ReturnOrderCellFactory(new HandleOpenOrderFormListener() {
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
        
        ArrayList<ReturnMoveOut> returnMoveOuts = new ArrayList<ReturnMoveOut>();
        ReturnOrder cloneReturnOrder = returnOrder.clone(returnMoveOuts);

        returnMovementListView = new ListView<ReturnMoveOut>();
        ReturnMoveOutCellFactory returnMovementCellFactory = new ReturnMoveOutCellFactory();
        returnMovementListView.setCellFactory(returnMovementCellFactory);
        // ReturnMoveOut returnMoveOut = listView.getItems().get(cellFactory.getSelectedItemIndex());
        // obtains ReturnMoveOutList from ReturnOrder to observableReturnMoveOutList and refill ListView
        List<ReturnMoveOut> cloneReturnMoveOuts = new ArrayList<ReturnMoveOut>();
        for(SoftReference<ReturnMoveOut> softCloneReturnMoveOut : cloneReturnOrder.getReturnMoveOutList()){
            cloneReturnMoveOuts.add(softCloneReturnMoveOut.get());
        }
        observableReturnMoveOutList.addAll(cloneReturnMoveOuts);
        refillMovementListView(observableReturnMoveOutList);
        
        TextField orderIdText = new TextField();
        orderIdText.setEditable(false);
        orderIdText.getStyleClass().add("copiable-text");
        orderIdText.setText(cloneReturnOrder.getId());
        
        TextField scaningSearchBar = new TextField();

        scaningSearchBar.setOnKeyPressed((keyEvent) ->{
            if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                ReturnMoveOut returnMoveOut = imputer.getReturnMoveOuInOrder(cloneReturnOrder, scaningSearchBar.getText());
                
                if(returnMoveOut == null) {
                    scaningSearchBar.selectAll();
                    return;
                }
                
                //application logic about fill return quantity to relative movement. 
                if(returnMoveOut.getQuantity() > returnMoveOut.getStatusQuantity()) {
                    // only increase when it not more than send out.
                    returnMoveOut.setQuantity(returnMoveOut.getStatusQuantity() + 1);
                }
                if(returnMoveOut.getReturnStatus().equals(ReturnMoveOut.RETURNING)) 
                    returnMoveOut.setReturnStatus(ReturnMoveOut.PARTICULAR_RECEIVED);
                if(returnMoveOut.getQuantity() == returnMoveOut.getStatusQuantity() 
                    && returnMoveOut.getReturnStatus().equals(ReturnMoveOut.PARTICULAR_RECEIVED)) {
                    returnMoveOut.setReturnStatus(ReturnMoveOut.RECEIVED);
                }
            }
        });
        scaningSearchBar.textProperty().addListener((observe, oldValue, newValue) -> {
            ArrayList<ReturnMoveOut> newReturnMoveOutList = new ArrayList<ReturnMoveOut>();

            if(newValue.isBlank() || newValue.isEmpty()) {
                refillMovementListView(FXCollections.observableArrayList(cloneReturnMoveOuts));\
                return;
            }
            // filter returnMoveOut contains newValue from searchBar
            for(ReturnMoveOut returnMoveOut : cloneReturnMoveOuts){
                if(returnMoveOut.getProductName().contains(newValue)) {
                    newReturnMoveOutList.add(returnMoveOut);
                } else if(returnMoveOut.getVariationName().contains(newValue)){
                    newReturnMoveOutList.add(returnMoveOut); 
                }
            }
            refillMovementListView(FXCollections.observableArrayList(newReturnMoveOutList));
        });
        scaningSearchBar.focusedProperty().addListener((observe, oldValue, newValue) ->{
            Platform.runLater(() ->{
                if(scaningSearchBar.isFocused() && !scaningSearchBar.getText().isEmpty()) scaningSearchBar.selectAll();
            });
        });

        Button splitMovementButton = new Button("Split Movement");
        splitMovementButton.setTooltip(new Tooltip("it let single movement split to two movemnt with different status"));
        boolean returnMovementSelecting = false;
        splitMovementButton.setOnAction((e) ->{
            
            
            // do split ReturnMoveOut UI ingretation and core.
            if(returnMovementSelecting && returnMovementSelecting.getSelected() != null){
                // TODO progress for #let user split return moveOuts into different status
                
            }
            
            
            // after work switch button display with two in one
            if(returnMovementSelecting == false) {
                splitMovementButton.setText("Spliting it");
                returnMovementSelecting = true;
            }
            if(returnMovementSelecting == true) {
                splitMovementButton.setText("Split Movement");
                returnMovementSelecting = false;
            }
            returnMovementCellFactory.setSelecting(returnMovementSelecting);
            refillMovementListView(observableReturnMoveOutList);
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction((e) -> {
            returnOrder.update(cloneReturnOrder);
            imputer.setUpdated(returnOrder);
            cleanHandleReturnMovementScene();
        });

        Button backButton = new Button("back to Orders");
        backButton.setOnAction((e) -> cleanHandleReturnMovementScene());

        Pane spacer = new Pane();
        spacer.setMinSize(10, 1);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox headerPanel = new HBox(orderIdText, scaningSearchBar, spacer, backButton, saveButton);
        headerPanel.setPadding(new Insets(10, 10, 10, 10));

        Text productDescriptionHeaderText = new Text("Description");
        Text skuHeaderText = new Text("SKU");
        Text quantityHeaderText = new Text("Quantity");
        Text returnQuantityHeaderText = new Text("to Return QTY");
        productDescriptionHeaderText.setWrappingWidth(137);
        skuHeaderText.setWrappingWidth(90);
        quantityHeaderText.setWrappingWidth(30);
        returnQuantityHeaderText.setWrappingWidth(50);
        
        HBox listViewHeaderHBox = new Hbox(productDescriptionHeaderText, skuHeaderText, quantityHeaderText, returnQuantityHeaderText);

        
        Scene subScene = new Scene(new VBox(headerPanel, listViewHeaderHBox, returnMovementListView, saveButton));
        subScene.getStylesheets().add(getClass().getResource("copiable-text.css").toExternalForm());
        
        subScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination saveKeyCombination = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_ANY);
            
            @Override
            public void handle(KeyEvent event) {
                if(saveKeyCombination.match(event)){
                    returnOrder.update(cloneReturnOrder);
                    imputer.setUpdated(returnOrder);
                    cleanHandleReturnMovementScene();
                }
                event.consume();
            }
            
        });

        pushScene(subScene);
    }

    private void cleanHandleReturnMovementScene(){
        popScene();
        observableReturnMoveOutList = FXCollections.observableArrayList();
        refillMovementListView(observableReturnMoveOutList);
    }

    private void refillMovementListView(ObservableList<ReturnMoveOut> returnMoveOuts){
        returnMovementListView.setItems(FXCollections.emptyObservableList());
        returnMovementListView.setItems(observableReturnMoveOutList);
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
