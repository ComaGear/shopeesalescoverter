package com.colbertlum.Controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hpsf.Array;

import com.colbertlum.Imputer.MeasImputer;
import com.colbertlum.Imputer.SalesImputer;
import com.colbertlum.cellFactory.SalesCellFactory;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.MoveOutReason;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SalesImputingController {

    private static final String FILTER_EMPTY_SKU = "EMPTY_SKU";
    private static final String FILTER_ALL = "All";
    private Stage stage;
    private String moveOutSearchMode;
    private SalesImputer salesImputer;
    private MeasImputingController measImputingController;
    private ArrayList<MoveOutReason> selectedMoveOutStatusList;
    private ObservableList<MoveOutReason> observableMoveOutStatusList;
    private String filterMode;

    public void initDialog(Stage stage){    
        this.stage = stage;
        
        stage.setTitle("sales Imputer");
        stage.setWidth(1400);
        stage.setHeight(600);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {

                if(salesImputer.isMoveOutChanged()){
                    salesImputer.saveChange();
                }
                if(measImputingController != null) measImputingController.close();
            }
            
        });

        VBox moveOutListViewPanel = this.generateMoveOutListViewPanel();

        VBox measListViewPanel = measImputingController.generateMeasListViewPanel();

        VBox measPanel = measImputingController.generatePanel();
        
        VBox vBox = new VBox(moveOutListViewPanel, measListViewPanel);
        HBox hBox = new HBox(vBox, measPanel);
        stage.setScene(new Scene(hBox));
    }

    
    public Stage getStage() {
        return this.stage;
    }

    private void refillMoveOutListView(ListView<MoveOutReason> listView, List<MoveOutReason> moveOutStatusList){
        this.observableMoveOutStatusList.clear();

        ArrayList<MoveOutReason> arrayList = new ArrayList<MoveOutReason>();
        if(filterMode == FILTER_ALL){
            arrayList.addAll(moveOutStatusList);
        } else if(filterMode == FILTER_EMPTY_SKU){
            for(MoveOutReason moveOutStatus : moveOutStatusList){
                if(moveOutStatus.getMoveOut().getSku().isEmpty()) {
                    arrayList.add(moveOutStatus);
                }
            }
        }
        

        this.observableMoveOutStatusList.addAll(arrayList);
        listView.setItems(observableMoveOutStatusList);
    }
    
    private VBox generateMoveOutListViewPanel(){
        TextField searchBar = new TextField("");
        searchBar.setPromptText("search item by NAME");
        searchBar.setMinWidth(300);

        MenuItem skuSelectMenuItem = new MenuItem(SalesImputer.SKU);
        MenuItem nameSelectMenuItem = new MenuItem(SalesImputer.NAME);
        MenuButton SearchByMenuButton = new MenuButton("search by NAME", null, skuSelectMenuItem, nameSelectMenuItem);
        SearchByMenuButton.setPrefWidth(120);
        skuSelectMenuItem.setOnAction(a ->{
            SearchByMenuButton.setText("search by SKU");
            searchBar.setPromptText("search item by SKU");
            this.moveOutSearchMode = SalesImputer.SKU;
        });
        nameSelectMenuItem.setOnAction(a ->{
            SearchByMenuButton.setText("search by NAME");
            searchBar.setPromptText("search item by NAME");
            this.moveOutSearchMode = SalesImputer.NAME;
        });
        this.moveOutSearchMode = SalesImputer.NAME;

        MenuItem filterEmptyMenuItem = new MenuItem("empty sku");
        MenuItem filterAllMenuItem = new MenuItem(FILTER_ALL);
        MenuButton filterMenuButton = new MenuButton("filter by All", null, filterEmptyMenuItem, filterAllMenuItem);
        this.filterMode = FILTER_ALL;
        filterMenuButton.setPrefWidth(120);

        Button applyButton = new Button("Apply To");
        HBox moveOutsSearchHBox = new HBox(filterMenuButton, SearchByMenuButton, searchBar, applyButton);

        Text skuHeaderText = new Text("SKU");
        skuHeaderText.setWrappingWidth(117);
        Text nameHeaderText = new Text(SalesImputer.NAME);
        nameHeaderText.setWrappingWidth(450);
        Text variationHeaderText = new Text("VARIATION");
        variationHeaderText.setWrappingWidth(200);
        Text foundRowHeader = new Text("ROW POSITION");
        foundRowHeader.setWrappingWidth(100);
        Text statusHeader = new Text("STATUS");
        statusHeader.setWrappingWidth(100);

        HBox headerHBox = new HBox(skuHeaderText, nameHeaderText, variationHeaderText, foundRowHeader, statusHeader);

        ListView<MoveOutReason> moveOutListView = new ListView<MoveOutReason>();
        SalesCellFactory salesCellFactory = new SalesCellFactory(this.selectedMoveOutStatusList);
        moveOutListView.setCellFactory(salesCellFactory);
        this.observableMoveOutStatusList = FXCollections.observableArrayList();
        refillMoveOutListView(moveOutListView, salesImputer.getMoveOutStatusList());

        filterAllMenuItem.setOnAction(e -> {
            filterMenuButton.setText("filter by All");
            filterMode = FILTER_ALL;
            refillMoveOutListView(moveOutListView, salesImputer.getMoveOutStatusList());
        });
        filterEmptyMenuItem.setOnAction(e ->{
            filterMenuButton.setText("filter by Emptry");
            filterMode = FILTER_EMPTY_SKU;
            refillMoveOutListView(moveOutListView, salesImputer.getMoveOutStatusList());
        });

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
            ArrayList<MoveOutReason> matchedMoveOutStatusList = new ArrayList<MoveOutReason>();
            String[] splitStr = searchBar.getText().split("\\s+");
            
            for(MoveOutReason moveOutStatus : salesImputer.getMoveOutStatusList()){
                MoveOut moveOut = moveOutStatus.getMoveOut();
                String matchStr = null;
                switch(this.moveOutSearchMode){
                    case SalesImputer.NAME: 
                        if(moveOut.getProductName() == null) continue;
                        matchStr = moveOut.getProductName().toLowerCase();
                        break;
                    case SalesImputer.SKU:
                        if(moveOut.getSku() == null) continue;
                        matchStr = moveOut.getSku().toLowerCase();
                        break;
                    default:
                        return;
                }
                int i = 0;
                int match = 0;

                while(i < splitStr.length && !(matchStr.indexOf(splitStr[i].toLowerCase()) < 0)){

                    int subIndex = matchStr.indexOf(splitStr[i].toLowerCase());
                    matchStr = matchStr.substring(subIndex+splitStr[i].length(), matchStr.length());
                    match++;
                    i++;
                }

                if(i >= splitStr.length && match >= splitStr.length){
                    matchedMoveOutStatusList.add(moveOutStatus);
                }
            }

            moveOutListView.getItems().clear();
            if(newValue.isEmpty()){
                matchedMoveOutStatusList = salesImputer.getMoveOutStatusList();
            }
            refillMoveOutListView(moveOutListView, matchedMoveOutStatusList);
        });
        
        applyButton.setOnAction(event ->{
            if(selectedMoveOutStatusList == null || selectedMoveOutStatusList.isEmpty()) return;
            for(MoveOutReason moveOutStatus : selectedMoveOutStatusList){
                // MoveOutStatus moveOutStatus = salesImputer.getMoveOutFromStatusListByFoundRow(foundRow);
                String selectedMeasSku = measImputingController.getSelectedMeasSku();
                if(selectedMeasSku == null) moveOutStatus.getMoveOut().setSku("");
                else moveOutStatus.getMoveOut().setSku(selectedMeasSku);

                salesImputer.setMoveOutChanged(true);
            }

            selectedMoveOutStatusList.clear();

            refillMoveOutListView(moveOutListView, salesImputer.getMoveOutStatusList());
        });

        return new VBox(moveOutsSearchHBox, headerHBox, moveOutListView);
    }

    public Scene getScene(){
        VBox moveOutListViewPanel = this.generateMoveOutListViewPanel();

        VBox measListViewPanel = measImputingController.generateMeasListViewPanel();

        VBox measPanel = measImputingController.generatePanel();
        
        VBox vBox = new VBox(moveOutListViewPanel, measListViewPanel);
        HBox hBox = new HBox(vBox, measPanel);
        return new Scene(hBox);
    }

    public SalesImputingController(List<MoveOut> emptySkuMoveOuts, List<MoveOut> notExistSkuMoveOuts){
        this.salesImputer = new SalesImputer(emptySkuMoveOuts, notExistSkuMoveOuts);
        this.measImputingController = new MeasImputingController();
        this.selectedMoveOutStatusList = new ArrayList<MoveOutReason>();
    }
}
