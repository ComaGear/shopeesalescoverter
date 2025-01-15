package com.colbertlum.Controller;

import java.util.ArrayList;
import java.util.List;

import com.colbertlum.Imputer.StockImputer;
import com.colbertlum.cellFactory.OnlineSalesInfoStatusCellFactory;

import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.OnlineSalesInfoReason;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StockImputingController {

    private Stage imputerStage;
    private List<OnlineSalesInfoReason> onlineSalesInfoStatusList;
    private List<OnlineSalesInfoReason> selectOnlineSalesList;
    private MeasImputingController measImputingController;
    private ObservableList<OnlineSalesInfoReason> observableOnlineInfoList;
    private ListView<OnlineSalesInfoReason> onlineSalesInfoStatusListView;

    public StockImputingController(Stage imputerStage, List<OnlineSalesInfoReason> onlineSalesInfoStatusList) {
        this.imputerStage = imputerStage;
        imputerStage.setWidth(1400);
        imputerStage.setHeight(600);
        this.imputerStage.setTitle("Online Sales Info Imputer");
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
        this.selectOnlineSalesList = new ArrayList<OnlineSalesInfoReason>();
        measImputingController = new MeasImputingController();

        imputerStage.setOnCloseRequest(e ->{
            measImputingController.close();
        });
    }

    public void initStage() {
        
        onlineSalesInfoStatusListView = new ListView<OnlineSalesInfoReason>();
        onlineSalesInfoStatusListView.setCellFactory(new OnlineSalesInfoStatusCellFactory(this.selectOnlineSalesList));
        observableOnlineInfoList = FXCollections.observableArrayList();
        observableOnlineInfoList.setAll(this.onlineSalesInfoStatusList);
        onlineSalesInfoStatusListView.getItems().setAll(observableOnlineInfoList);

        MenuItem emptySkuMenuItem = new MenuItem("empty sku");
        MenuItem manualSetMenuItem = new MenuItem("manual set");
        MenuItem notExistSkuMenuItem = new MenuItem("not exist sku");
        MenuItem notExistProductIdMenuItem = new MenuItem("not exist product id");
        MenuButton viewByMenuButton = new MenuButton("View All", null, emptySkuMenuItem, manualSetMenuItem, notExistSkuMenuItem, notExistProductIdMenuItem);
        viewByMenuButton.setPrefWidth(150);
        emptySkuMenuItem.setOnAction(a -> {
            viewByMenuButton.setText("view by " + emptySkuMenuItem.getText());
            refillOnlineListView(filterStatusBy(StockImputer.EMPTY_SKU));
        });
        manualSetMenuItem.setOnAction(a -> {
            viewByMenuButton.setText("view by " + manualSetMenuItem.getText());
            refillOnlineListView(filterStatusBy(StockImputer.MANUAL_SET_STOCK_STATUS));
        });
        notExistSkuMenuItem.setOnAction(a -> {
            viewByMenuButton.setText("view by " + notExistSkuMenuItem.getText());
            refillOnlineListView(filterStatusBy(StockImputer.NOT_EXIST_SKU_STATUS));
        });
        notExistProductIdMenuItem.setOnAction(a -> {
            viewByMenuButton.setText("view by " + notExistProductIdMenuItem.getText());
            refillOnlineListView(filterStatusBy(StockImputer.NOT_EXIST_PRODUCT_ID_STATUS));
        });


        Button applyMeasButton = new Button("apply To");
        applyMeasButton.setOnAction(a ->{
            for(OnlineSalesInfoReason status : selectOnlineSalesList) {
                String selectedMeasSku = measImputingController.getSelectedMeasSku();
                if(selectedMeasSku != null && !selectedMeasSku.isEmpty() && selectedMeasSku.contains("-")){
                    status.getOnlineSalesInfo().setSku(selectedMeasSku);
                } else if(selectedMeasSku != null && !selectedMeasSku.isEmpty()) {
                    status.getOnlineSalesInfo().setParentSku(selectedMeasSku);
                }
                
                selectOnlineSalesList.clear();
                refillOnlineListView(new ArrayList<OnlineSalesInfoReason>(observableOnlineInfoList));
            }
        });

        TextField stockField = new TextField();
        stockField.setPromptText("stock apply to all");
        stockField.setPrefWidth(80);
        stockField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                stockField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        Button applyStockButton = new Button("Apply Stock to");
        applyStockButton.setOnAction(a -> {
            int stock = Integer.parseInt(stockField.getText());
            stockField.clear();

            for(OnlineSalesInfoReason infoStatus : selectOnlineSalesList){
                infoStatus.getOnlineSalesInfo().setQuantity(stock);
            }

            selectOnlineSalesList.clear();
            refillOnlineListView(new ArrayList<OnlineSalesInfoReason>(observableOnlineInfoList));
        });

        Region onlineListViewOperationSpacer = new Region();
        HBox.setHgrow(onlineListViewOperationSpacer, Priority.ALWAYS);
        HBox onlineListViewOperationHBox = new HBox(viewByMenuButton, applyMeasButton, onlineListViewOperationSpacer, stockField, applyStockButton);
        onlineListViewOperationHBox.setPadding(new Insets(2, 5, 2, 5));

        Text checkBoxText = new Text();
        checkBoxText.setWrappingWidth(30);
        Text productNameHeaderText = new Text("PRODUCT NAME");
        productNameHeaderText.setWrappingWidth(500);
        Text variationNameHeaderText = new Text("VARIATION NAME");
        variationNameHeaderText.setWrappingWidth(200);
        Text skuHeaderText = new Text("SKU");
        skuHeaderText.setWrappingWidth(80);
        Text stockHeaderText = new Text("STOCK");
        stockHeaderText.setWrappingWidth(50);
        Text priceHeaderText = new Text("PRICE");
        priceHeaderText.setWrappingWidth(50);
        Text statusHeaderText = new Text("STATUS");
        statusHeaderText.setWrappingWidth(150);

        
        HBox onlineSalesListViewHeader = new HBox(checkBoxText, productNameHeaderText, variationNameHeaderText, skuHeaderText
            , stockHeaderText, priceHeaderText, statusHeaderText);
        onlineSalesListViewHeader.setPadding(new Insets(2, 5, 2, 5));

        VBox onlineSalesInfoPanel = new VBox(onlineListViewOperationHBox, onlineSalesListViewHeader, onlineSalesInfoStatusListView);

        VBox listViewPanel = new VBox(onlineSalesInfoPanel, measImputingController.generateMeasListViewPanel());

        this.imputerStage.setScene(new Scene(new HBox(listViewPanel, measImputingController.generatePanel())));
    }

    private void refillOnlineListView(List<OnlineSalesInfoReason> onlineSalesInfoStatusList) {
        this.observableOnlineInfoList.clear();
        this.observableOnlineInfoList.addAll(onlineSalesInfoStatusList);
        this.onlineSalesInfoStatusListView.setItems(observableOnlineInfoList);
    }

    private List<OnlineSalesInfoReason> filterStatusBy(String string) {
        ArrayList<OnlineSalesInfoReason> arrayList = new ArrayList<OnlineSalesInfoReason>();
        for(OnlineSalesInfoReason status : onlineSalesInfoStatusList){
            if(status.getStatus().equals(string)) arrayList.add(status);
        }
        return arrayList;
    }

    public Stage getStage() {
        return imputerStage;
    }

    public List<OnlineSalesInfo> getFixedOnlineInfo() {
        ArrayList<OnlineSalesInfo> arrayList = new ArrayList<OnlineSalesInfo>();
        for(OnlineSalesInfoReason infoStatus : this.onlineSalesInfoStatusList){
            arrayList.add(infoStatus.getOnlineSalesInfo());
        }
        return arrayList;
    }


}
