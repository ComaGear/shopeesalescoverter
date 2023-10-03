package com.colbertlum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SalesImputer {

    private static final String NAME = "NAME";
    private static final String SKU = "SKU";

    private Stage stage;
    private ArrayList<MoveOutStatus> moveOutStatusList;
    private Meas selectedMeas;
    private ArrayList<Meas> measList;
    private String moveOutSearchMode;

    public SalesImputer(List<MoveOut> emptySkuMoveOuts, List<MoveOut> notExistSkuMoveOuts) {

        if(emptySkuMoveOuts == null && notExistSkuMoveOuts == null) throw new NullPointerException();
        
        moveOutStatusList = new ArrayList<MoveOutStatus>();
        if(emptySkuMoveOuts != null){
            for(MoveOut moveOut: emptySkuMoveOuts){
                moveOutStatusList.add(new MoveOutStatus(MoveOutStatus.EMPTY, moveOut));
            }
        }
        
        if(notExistSkuMoveOuts != null){
            for(MoveOut moveOut: notExistSkuMoveOuts){
                moveOutStatusList.add(new MoveOutStatus(MoveOutStatus.NOT_EXIST_SKU, moveOut));
            }
        }
    }
    
    public void setMeasList(ArrayList<Meas> measList){
        this.measList = measList;
        if(measList.get(0).getName() == null) MeasImputer.imputeNameField(measList);
    }

    public void initDialog(Stage stage){
        this.stage = stage;
        
        stage.setTitle("sales Imputer");
        stage.setWidth(1000);
        stage.setHeight(500);


        VBox moveOutListViewPanel = this.generateMoveOutListViewPanel();

        VBox measListVieewPanel = this.generateMeasListViewPanel();
        
        VBox vBox = new VBox(moveOutListViewPanel, measListVieewPanel);
        stage.setScene(new Scene(vBox));
    }

    private VBox generateMeasListViewPanel() {

        // TextField searchBar = new TextField("");
        // searchBar.setPromptText("search item by NAME");
        // searchBar.setMinWidth(300);
        // MenuItem skuSelectMenuItem = new MenuItem(SKU);
        // MenuItem nameSelectMenuItem = new MenuItem(NAME);
        // MenuButton menuButton = new MenuButton("search by NAME", null, skuSelectMenuItem, nameSelectMenuItem);
        // menuButton.setPrefWidth(120);

        // skuSelectMenuItem.setOnAction(a ->{
        //     menuButton.setText("search by SKU");
        //     searchBar.setPromptText("search item by SKU");
        //     this.moveOutSearchMode = SKU;
        // });
        // nameSelectMenuItem.setOnAction(a ->{
        //     menuButton.setText("search by NAME");
        //     searchBar.setPromptText("search item by NAME");
        //     this.moveOutSearchMode = NAME;
        // });
        // this.moveOutSearchMode = NAME;

        // Button applyButton = new Button("Apply To");
        // HBox moveOutsSearchHBox = new HBox(menuButton, searchBar, applyButton);

        // Text skuHeaderText = new Text("SKU");
        // skuHeaderText.setWrappingWidth(97);
        // Text nameHeaderText = new Text(NAME);
        // nameHeaderText.setWrappingWidth(650);
        // Text foundRowHeader = new Text("ROW POSITION");
        // foundRowHeader.setWrappingWidth(100);
        // Text statusHeader = new Text("STATUS");
        // statusHeader.setWrappingWidth(100);

        // HBox headerHBox = new HBox(skuHeaderText, nameHeaderText, foundRowHeader, statusHeader);

        // ListView<HBox> moveOutListView = new ListView<HBox>();
        // for(MoveOutStatus moveOutStatus :  moveOutStatusList){

        //     Text skuText = new Text(moveOutStatus.getMoveOut().getSku());
        //     skuText.setWrappingWidth(90);
        //     Text nameText = new Text(moveOutStatus.getMoveOut().getProductName());
        //     nameText.setWrappingWidth(650);
        //     Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
        //     foundRow.setWrappingWidth(100);
        //     Text status = new Text(moveOutStatus.getStatus());
        //     status.setWrappingWidth(100);

        //     moveOutListView.getItems().add(new HBox(skuText, nameText, foundRow, status));
        // }

        // moveOutListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
        //     ArrayList<MoveOutStatus> matchedMoveOutStatusList = new ArrayList<MoveOutStatus>();
        //     String[] splitStr = searchBar.getText().split("\\s+");
            
        //     for(MoveOutStatus moveOutStatus : moveOutStatusList){
        //         MoveOut moveOut = moveOutStatus.getMoveOut();
        //         String matchStr = null;
        //         switch(this.moveOutSearchMode){
        //             case NAME: 
        //                 if(moveOut.getProductName() == null) continue;
        //                 matchStr = moveOut.getProductName().toLowerCase();
        //                 break;
        //             case SKU:
        //                 if(moveOut.getSku() == null) continue;
        //                 matchStr = moveOut.getSku().toLowerCase();
        //                 break;
        //             default:
        //                 return;
        //         }
        //         int i = 0;
        //         int match = 0;

        //         while(i < splitStr.length && !(matchStr.indexOf(splitStr[i].toLowerCase()) < 0)){

        //             int subIndex = matchStr.indexOf(splitStr[i].toLowerCase());
        //             matchStr = matchStr.substring(subIndex+splitStr[i].length(), matchStr.length());
        //             match++;
        //             i++;
        //         }

        //         if(i >= splitStr.length && match >= splitStr.length){
        //             matchedMoveOutStatusList.add(moveOutStatus);
        //         }
        //     }

        //     moveOutListView.getItems().clear();
        //     if(newValue.isEmpty()){
        //         matchedMoveOutStatusList = this.moveOutStatusList;
        //     }
        //     for(MoveOutStatus moveOutStatus :  matchedMoveOutStatusList){

        //         Text skuText = new Text(moveOutStatus.getMoveOut().getSku());
        //         skuText.setWrappingWidth(90);
        //         Text nameText = new Text(moveOutStatus.getMoveOut().getProductName());
        //         nameText.setWrappingWidth(650);
        //         Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
        //         foundRow.setWrappingWidth(100);
        //         Text status = new Text(moveOutStatus.getStatus());
        //         status.setWrappingWidth(100);

        //         moveOutListView.getItems().add(new HBox(skuText, nameText, foundRow, status));
        //     }
        // });
        TextField measSearchTextField = new TextField("");
        MenuItem meaSkuSelectMenuItem = new MenuItem(SKU);
        MenuItem meaNameSelectMenuItem = new MenuItem("Name");
        MenuButton measMenuButton = new MenuButton("search by", null, meaSkuSelectMenuItem, meaNameSelectMenuItem);
        HBox measSearchHBox = new HBox(measMenuButton, measSearchTextField);

        ListView<HBox> measListView = new ListView<HBox>();
        for(Meas mea :  measList){

            Text relativeIdText = new Text(mea.getRelativeId());
            Text nameText = new Text(mea.getName());
            Text measuremenText = new Text(Double.toString(mea.getMeasurement()));
            Text idText = new Text(mea.getId());
            Text updateRuleText = new Text(mea.getUpdateRule());

            measListView.getItems().add(new HBox(relativeIdText, nameText, measuremenText, idText, updateRuleText));
        }

        return new VBox(measSearchHBox, measListView);
    }

    private MoveOutStatus getMoveOutFromStatusListByFoundRow(String foundRow) {
        if(moveOutStatusList == null) return null;
        
        for(MoveOutStatus moveOutStatus : moveOutStatusList){
            if(Integer.parseInt(foundRow) == moveOutStatus.getMoveOut().getFoundRow()){
                return moveOutStatus;
            }
        }
        return null;
    }

    public Stage getStage() {
        return this.stage;
    }
    
    private VBox generateMoveOutListViewPanel(){
        TextField searchBar = new TextField("");
        searchBar.setPromptText("search item by NAME");
        searchBar.setMinWidth(300);
        MenuItem skuSelectMenuItem = new MenuItem(SKU);
        MenuItem nameSelectMenuItem = new MenuItem(NAME);
        MenuButton menuButton = new MenuButton("search by NAME", null, skuSelectMenuItem, nameSelectMenuItem);
        menuButton.setPrefWidth(120);

        skuSelectMenuItem.setOnAction(a ->{
            menuButton.setText("search by SKU");
            searchBar.setPromptText("search item by SKU");
            this.moveOutSearchMode = SKU;
        });
        nameSelectMenuItem.setOnAction(a ->{
            menuButton.setText("search by NAME");
            searchBar.setPromptText("search item by NAME");
            this.moveOutSearchMode = NAME;
        });
        this.moveOutSearchMode = NAME;

        Button applyButton = new Button("Apply To");
        HBox moveOutsSearchHBox = new HBox(menuButton, searchBar, applyButton);

        Text skuHeaderText = new Text("SKU");
        skuHeaderText.setWrappingWidth(97);
        Text nameHeaderText = new Text(NAME);
        nameHeaderText.setWrappingWidth(650);
        Text foundRowHeader = new Text("ROW POSITION");
        foundRowHeader.setWrappingWidth(100);
        Text statusHeader = new Text("STATUS");
        statusHeader.setWrappingWidth(100);

        HBox headerHBox = new HBox(skuHeaderText, nameHeaderText, foundRowHeader, statusHeader);

        ListView<HBox> moveOutListView = new ListView<HBox>();
        for(MoveOutStatus moveOutStatus :  moveOutStatusList){

            Text skuText = new Text(moveOutStatus.getMoveOut().getSku());
            skuText.setWrappingWidth(90);
            Text nameText = new Text(moveOutStatus.getMoveOut().getProductName());
            nameText.setWrappingWidth(650);
            Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
            foundRow.setWrappingWidth(100);
            Text status = new Text(moveOutStatus.getStatus());
            status.setWrappingWidth(100);

            moveOutListView.getItems().add(new HBox(skuText, nameText, foundRow, status));
        }

        moveOutListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
            ArrayList<MoveOutStatus> matchedMoveOutStatusList = new ArrayList<MoveOutStatus>();
            String[] splitStr = searchBar.getText().split("\\s+");
            
            for(MoveOutStatus moveOutStatus : moveOutStatusList){
                MoveOut moveOut = moveOutStatus.getMoveOut();
                String matchStr = null;
                switch(this.moveOutSearchMode){
                    case NAME: 
                        if(moveOut.getProductName() == null) continue;
                        matchStr = moveOut.getProductName().toLowerCase();
                        break;
                    case SKU:
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
                matchedMoveOutStatusList = this.moveOutStatusList;
            }
            for(MoveOutStatus moveOutStatus :  matchedMoveOutStatusList){

                Text skuText = new Text(moveOutStatus.getMoveOut().getSku());
                skuText.setWrappingWidth(90);
                Text nameText = new Text(moveOutStatus.getMoveOut().getProductName());
                nameText.setWrappingWidth(650);
                Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
                foundRow.setWrappingWidth(100);
                Text status = new Text(moveOutStatus.getStatus());
                status.setWrappingWidth(100);

                moveOutListView.getItems().add(new HBox(skuText, nameText, foundRow, status));
            }
        });
        
        applyButton.setOnAction(event ->{
            ObservableList<HBox> selectedItems = moveOutListView.getSelectionModel().getSelectedItems();
            for(HBox i : selectedItems){
                Text text = (Text)i.getChildren().get(3);
                String foundRow = text.getText();
                MoveOutStatus moveOutStatus = getMoveOutFromStatusListByFoundRow(foundRow);
                moveOutStatus.getMoveOut().setSku(selectedMeas.getRelativeId());
            }
        });

        return new VBox(moveOutsSearchHBox, headerHBox, moveOutListView);
    }

    private class MoveOutStatus{

        public static final String EMPTY = "Empty sku";
        public static final String NOT_EXIST_SKU = "SKU not found at meas";
        
        private MoveOut moveOut;
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public MoveOut getMoveOut() {
            return moveOut;
        }

        public void setMoveOut(MoveOut moveOut) {
            this.moveOut = moveOut;
        }

        public MoveOutStatus(String status, MoveOut moveOut){
            this.status = status;
            this.moveOut = moveOut;
        }
    }
}
