package com.colbertlum;

import java.util.ArrayList;
import java.util.List;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.OnlineSalesInfoStatus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class StockImputingController {

    private Stage imputerStage;
    private List<OnlineSalesInfoStatus> onlineSalesInfoStatusList;
    private List<OnlineSalesInfoStatus> selectOnlineSalesList;
    private MeasImputer measImputer;
    private ObservableList<OnlineSalesInfoStatus> observableOnlineInfoList;

    public StockImputingController(Stage imputerStage, List<OnlineSalesInfoStatus> onlineSalesInfoStatusList) {
        this.imputerStage = imputerStage;
        imputerStage.setWidth(1400);
        imputerStage.setHeight(600);
        this.imputerStage.setTitle("Online Sales Info Imputer");
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
        this.selectOnlineSalesList = new ArrayList<OnlineSalesInfoStatus>();
        measImputer = new MeasImputer();
    }

    public void initStage() {

        
        ListView<OnlineSalesInfoStatus> onlineSalesInfoStatusListView = new ListView<OnlineSalesInfoStatus>();
        onlineSalesInfoStatusListView.setCellFactory(new OnlineSalesInfoStatusCellFactory(this.selectOnlineSalesList));
        observableOnlineInfoList = FXCollections.observableArrayList();
        observableOnlineInfoList.setAll(this.onlineSalesInfoStatusList);
        onlineSalesInfoStatusListView.getItems().setAll(observableOnlineInfoList);

        MenuItem manualSetMenuItem = new MenuItem("manual set");
        MenuItem notExistSkuMenuItem = new MenuItem("not exist sku");
        MenuItem notExistProductIdMenuItem = new MenuItem("not exist product id");
        MenuButton viewByMenuButton = new MenuButton("View All", null, manualSetMenuItem, notExistSkuMenuItem, notExistProductIdMenuItem);
        viewByMenuButton.setPrefWidth(150);
        manualSetMenuItem.setOnAction(a -> {
            viewByMenuButton.setText("view by" + manualSetMenuItem.getText());
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
            for(OnlineSalesInfoStatus status : selectOnlineSalesList) {
                status.getOnlineSalesInfo().setSku(measImputer.getSelectedMeasSku());
            }
        });

        HBox onlineListViewOperationHBox = new HBox(viewByMenuButton, applyMeasButton);

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



        VBox onlineSalesInfoPanel = new VBox(onlineSalesListViewHeader, onlineSalesInfoStatusListView);

        VBox listViewPanel = new VBox(onlineSalesInfoPanel, measImputer.generateMeasListViewPanel(measImputer.getMeasList()));

        this.imputerStage.setScene(new Scene(new HBox(listViewPanel, measImputer.generatePanel())));
    }

    private void refillOnlineListView(List<OnlineSalesInfoStatus> onlineSalesInfoStatusList) {
        this.observableOnlineInfoList.clear();
        this.observableOnlineInfoList.addAll(onlineSalesInfoStatusList);
    }

    private List<OnlineSalesInfoStatus> filterStatusBy(String string) {
        ArrayList<OnlineSalesInfoStatus> arrayList = new ArrayList<OnlineSalesInfoStatus>();
        for(OnlineSalesInfoStatus status : onlineSalesInfoStatusList){
            if(status.getStatus().equals(string)) observableOnlineInfoList.add(status);
        }
        return arrayList;
    }

    public Stage getStage() {
        return imputerStage;
    }

    public List<OnlineSalesInfo> getFixedOnlineInfo() {
        return null;
    }


}
