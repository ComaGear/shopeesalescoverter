package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.MoveOut;

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

public class SalesImputer {

    /**
     *
     */
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String SKU = "SKU";

    private Stage stage;
    private ArrayList<MoveOutStatus> moveOutStatusList;
    private Meas selectedMeas;
    private ArrayList<Meas> measList;
    private String moveOutSearchMode;
    private String meaSearchMode;
    private String selectedMeasSku;
    private MeasImputer measImputer;
    protected boolean moveOutChanged;
    protected boolean measChanged;
    private String measSearchMode;

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
        if(measList.get(0).getName() == null) measImputer.imputeNameField(measList);
    }

    public void initDialog(Stage stage){
        this.stage = stage;
        
        stage.setTitle("sales Imputer");
        stage.setWidth(1400);
        stage.setHeight(600);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                if(moveOutChanged == true){
                    saveChange(moveOutStatusList);
                }
                if(measImputer != null && measImputer.isMeasChanged() == true){
                    measImputer.saveChange();
                }
            }
            
        });


        VBox moveOutListViewPanel = this.generateMoveOutListViewPanel();

        VBox measListViewPanel = this.generateMeasListViewPanel(this.measList);

        VBox measPanel = measImputer.generatePanel();
        
        VBox vBox = new VBox(moveOutListViewPanel, measListViewPanel);
        HBox hBox = new HBox(vBox, measPanel);
        stage.setScene(new Scene(hBox));
    }

    private VBox generateMeasListViewPanel(List<Meas> measList) {

        TextField searchBar = new TextField("");
        searchBar.setPromptText("search item by NAME");
        searchBar.setMinWidth(300);
        
        MenuItem skuSelectMenuItem = new MenuItem(SKU);
        MenuItem nameSelectMenuItem = new MenuItem(NAME);
        MenuItem idSellectMenuItem = new MenuItem(ID);
        MenuButton menuButton = new MenuButton("search by NAME", null, skuSelectMenuItem, nameSelectMenuItem, idSellectMenuItem);
        menuButton.setPrefWidth(120);

        measSearchMode = NAME;
        skuSelectMenuItem.setOnAction(a ->{
            menuButton.setText("search by SKU");
            searchBar.setPromptText("search item by SKU");
            measSearchMode = SKU;
        });
        nameSelectMenuItem.setOnAction(a ->{
            menuButton.setText("search by NAME");
            searchBar.setPromptText("search item by NAME");
            measSearchMode = NAME;
        });
        idSellectMenuItem.setOnAction(a ->{
            menuButton.setText("search by ID");
            searchBar.setPromptText("search item by ID");
            measSearchMode = ID;
        });


        Button editButton = new Button("edit it");
        HBox searchHBox = new HBox(menuButton, searchBar, editButton);

        Text skuHeaderText = new Text("SKU");
        skuHeaderText.setWrappingWidth(97);
        Text nameHeaderText = new Text(NAME);
        nameHeaderText.setWrappingWidth(650);
        Text rateHeader = new Text("RATE");
        rateHeader.setWrappingWidth(100);
        Text idHeader = new Text("ID");
        idHeader.setWrappingWidth(100);
        Text ruleHeader = new Text("RULE");
        ruleHeader.setWrappingWidth(100);

        HBox headerHBox = new HBox(skuHeaderText, nameHeaderText, rateHeader, idHeader, ruleHeader);

        ListView<HBox> measListView = new ListView<HBox>();
        refillMeasListView(measListView, measList);

        measListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        measListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            HBox hBox = observable.getValue();
            if(hBox != null){
                Text skuText = (Text) hBox.getChildren().get(0);
            this.selectedMeasSku = skuText.getText();
            }
        });

        editButton.setOnAction(a ->{
            HBox selectedItem = measListView.getSelectionModel().getSelectedItem();
            String relativeId = ((Text) selectedItem.getChildren().get(0)).getText();
            
            measList.sort(new Comparator<Meas>() {

                @Override
                public int compare(Meas o1, Meas o2) {
                    return o1.getRelativeId().toLowerCase().compareTo(o2.getRelativeId().toLowerCase());
                }
                
            });
            Meas meas = null;
            int lo = 0;
            int hi = measList.size()-1;
            while(lo <= hi) {
                int mid = lo + (hi-lo) / 2;
                if(measList.get(mid).getRelativeId().toLowerCase().compareTo(relativeId.toLowerCase()) > 0) hi = mid-1; 
                else if(measList.get(mid).getRelativeId().toLowerCase().compareTo(relativeId.toLowerCase()) < 0) lo = mid+1;
                else{
                    meas =  measList.get(mid);
                    break;
                }
            }

            if(meas != null){
                measImputer.changeButtonMode(MeasImputer.UPDATE);
                measImputer.editMeas(meas);
            }
        });

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
            ArrayList<Meas> matchedMeasList = new ArrayList<Meas>();
            String[] splitStr = searchBar.getText().split("\\s+");
            
            for(Meas meas : measList){
                String matchStr = null;
                switch(this.measSearchMode){
                    case NAME: 
                        if(meas.getName() == null) continue;
                        matchStr = meas.getName().toLowerCase();
                        break;
                    case SKU:
                        if(meas.getRelativeId() == null) continue;
                        matchStr = meas.getRelativeId().toLowerCase();
                        break;
                    case ID:
                        if(meas.getId() == null) continue;
                        matchStr = meas.getId().toLowerCase();
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
                    matchedMeasList.add(meas);
                }
            }

            measListView.getItems().clear();
            if(newValue.isEmpty()){
                matchedMeasList = this.measList;
            }
            refillMeasListView(measListView, matchedMeasList);
        });

        return new VBox(searchHBox, headerHBox, measListView);
    }

    private void refillMeasListView(ListView<HBox> measListView, List<Meas> measList) {
        for(Meas mea :  measList){

            Text relativeIdText = new Text(mea.getRelativeId());
            relativeIdText.setWrappingWidth(90);
            Text nameText = new Text(mea.getName());
            nameText.setWrappingWidth(650);
            Text measuremenText = new Text(Double.toString(mea.getMeasurement()));
            measuremenText.setWrappingWidth(100);
            Text idText = new Text(mea.getId());
            idText.setWrappingWidth(100);
            Text updateRuleText = new Text(mea.getUpdateRule());
            updateRuleText.setWrappingWidth(100);

            measListView.getItems().add(new HBox(relativeIdText, nameText, measuremenText, idText, updateRuleText));
        }
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
        nameHeaderText.setWrappingWidth(450);
        Text variationHeaderText = new Text("VARIATION");
        variationHeaderText.setWrappingWidth(200);
        Text foundRowHeader = new Text("ROW POSITION");
        foundRowHeader.setWrappingWidth(100);
        Text statusHeader = new Text("STATUS");
        statusHeader.setWrappingWidth(100);

        HBox headerHBox = new HBox(skuHeaderText, nameHeaderText, variationHeaderText, foundRowHeader, statusHeader);

        ListView<HBox> moveOutListView = new ListView<HBox>();
        refillMoveOutListView(moveOutListView, moveOutStatusList);

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
            refillMoveOutListView(moveOutListView, matchedMoveOutStatusList);
        });
        
        applyButton.setOnAction(event ->{
            ObservableList<HBox> selectedItems = moveOutListView.getSelectionModel().getSelectedItems();
            for(HBox i : selectedItems){
                Text text = (Text)i.getChildren().get(3);
                String foundRow = text.getText();
                MoveOutStatus moveOutStatus = getMoveOutFromStatusListByFoundRow(foundRow);
                moveOutStatus.getMoveOut().setSku(this.selectedMeasSku);
                refillMoveOutListView(moveOutListView, moveOutStatusList);

                this.moveOutChanged = true;
            }
        });

        return new VBox(moveOutsSearchHBox, headerHBox, moveOutListView);
    }

    private void refillMoveOutListView(ListView<HBox> listView, List<MoveOutStatus> moveOutStatusList){
        listView.getItems().clear();
        
        for(MoveOutStatus moveOutStatus :  moveOutStatusList){

            Text skuText = new Text(moveOutStatus.getMoveOut().getSku());
            skuText.setWrappingWidth(90);
            Text nameText = new Text(moveOutStatus.getMoveOut().getProductName());
            nameText.setWrappingWidth(450);
            Text variationText = new Text(moveOutStatus.getMoveOut().getVariationName());
            variationText.setWrappingWidth(200);
            Text foundRow = new Text(Integer.toString(moveOutStatus.getMoveOut().getFoundRow()));
            foundRow.setWrappingWidth(100);
            Text status = new Text(moveOutStatus.getStatus());
            status.setWrappingWidth(100);

            listView.getItems().add(new HBox(skuText, nameText, variationText, foundRow, status));
        }
    }

    private void saveChange(List<MoveOutStatus> resolveMoveOutStatus){
        String sourcePath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.REPORT);
        
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(sourcePath));
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for(MoveOutStatus moveOutStatus : resolveMoveOutStatus){
                int foundRow = moveOutStatus.getMoveOut().getFoundRow();
                Row row = sheet.getRow(foundRow);
                row.getCell(30).setCellValue(moveOutStatus.getMoveOut().getSku());
            }

            fileInputStream.close();

            FileOutputStream fileOutputStream = new FileOutputStream(new File(sourcePath));
            workbook.write(fileOutputStream);
            workbook.close();
            fileOutputStream.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public Scene getScene(){
        VBox moveOutListViewPanel = this.generateMoveOutListViewPanel();

        VBox measListViewPanel = this.generateMeasListViewPanel();

        VBox measPanel = measImputer.generatePanel();
        
        VBox vBox = new VBox(moveOutListViewPanel, measListViewPanel);
        HBox hBox = new HBox(vBox, measPanel);
        return new Scene(hBox);
    }

    public void setMeasImputer(MeasImputer measImputer) {
        this.measImputer = measImputer;
    }
}
