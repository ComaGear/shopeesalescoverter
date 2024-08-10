package com.colbertlum.Controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.colbertlum.Imputer.MeasImputer;
import com.colbertlum.Imputer.SalesImputer;
import com.colbertlum.cellFactory.MeasCellFactory;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.UOM;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MeasImputingController {

    private MeasImputer measImputer;
    private TextField productNameField;
    private String selectedProductId;
    private String measSearchMode;
    private ArrayList<Meas> selectedMeasList;
    private ObservableList<Meas> observableMeasList;
    private String buttonMode;
    private Button createButton;
    private TextField measurementField;
    private TextField updateRuleField;
    private Meas toEditMeas;
    private TextField parentSkuField;
    private TextField searchBar;
    private ListView<Meas> measListView;
    private Button updateIdButton;

    public MeasImputer getMeasImputer() {
        return measImputer;
    }

    private void refillMeasListView(ListView<Meas> measListView, List<Meas> measList) {
        this.observableMeasList.clear();
        this.observableMeasList.addAll(measList);
        measListView.setItems(observableMeasList);
    }

    private void refeshListView(){
        ArrayList<Meas> matchedMeasList = new ArrayList<Meas>();
        String[] splitStr = searchBar.getText().split("\\s+");
        
        for(Meas meas : measImputer.getMeasList()){
            String matchStr = null;
            switch(this.measSearchMode){
                case SalesImputer.NAME: 
                    if(meas.getName() == null) continue;
                    matchStr = meas.getName().toLowerCase();
                    break;
                case SalesImputer.SKU:
                    if(meas.getRelativeId() == null) continue;
                    matchStr = meas.getRelativeId().toLowerCase();
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

        if(searchBar.getText().isEmpty()){
            matchedMeasList = measImputer.getMeasList();
        }
        refillMeasListView(measListView, matchedMeasList);
    }

    public VBox generatedUOMListView(){
        ListView<HBox> uomListView = new ListView<HBox>();
        uomListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        for(UOM uom : measImputer.getIrsUoms()){
            Text descriptionText = new Text(uom.getDescription());
            Text idText = new Text(uom.getProductId());
            idText.setVisible(false);
            uomListView.getItems().add(new HBox(descriptionText, idText));
        }

        TextField searchBar = new TextField();
        searchBar.setPromptText("search by name");
        searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
            ArrayList<UOM> matchedUoms = new ArrayList<UOM>();
            String[] splitStr = searchBar.getText().split("\\s+");
            
            for(UOM uom : measImputer.getIrsUoms()){
                String matchStr = null;

                matchStr = uom.getDescription().toLowerCase();

                int i = 0;
                int match = 0;

                while(i < splitStr.length && !(matchStr.indexOf(splitStr[i].toLowerCase()) < 0)){

                    int subIndex = matchStr.indexOf(splitStr[i].toLowerCase());
                    matchStr = matchStr.substring(subIndex+splitStr[i].length(), matchStr.length());
                    match++;
                    i++;
                }

                if(i >= splitStr.length && match >= splitStr.length){
                    matchedUoms.add(uom);
                }
            }

            uomListView.getItems().clear();
            if(newValue.isEmpty()){
                matchedUoms = new ArrayList<UOM>(measImputer.getIrsUoms());
            }
            for(UOM uom : matchedUoms){
                Text descriptionText = new Text(uom.getDescription());
                Text idText = new Text(uom.getProductId());
                idText.setVisible(false);
                uomListView.getItems().add(new HBox(descriptionText, idText));
            }
        });
        
        Button useButton = new Button("use it");

        useButton.setOnAction(a ->{
            
            HBox selectedItem = uomListView.getSelectionModel().getSelectedItem();
            Text idText = (Text) selectedItem.getChildren().get(1);
            Text descriptionText = (Text) selectedItem.getChildren().get(0);
            
            if(productNameField != null){
                productNameField.setText((descriptionText.getText()));
                this.selectedProductId = idText.getText();
            }
        });

        updateIdButton = new Button("Update");
        updateIdButton.setDisable(true);
        updateIdButton.setOnAction(e->{
            HBox selectedItem = uomListView.getSelectionModel().getSelectedItem();
            Text idText = (Text) selectedItem.getChildren().get(1);
            
            if(toEditMeas == null) return;
            toEditMeas.setId(idText.getText());
        });

        return new VBox(new HBox(searchBar, useButton, updateIdButton), uomListView);
        
    }

    public VBox generateMeasListViewPanel() {

        searchBar = new TextField("");
        searchBar.setPromptText("search item by NAME");
        searchBar.setMinWidth(300);
        
        MenuItem skuSelectMenuItem = new MenuItem(SalesImputer.SKU);
        MenuItem nameSelectMenuItem = new MenuItem(SalesImputer.NAME);
        MenuButton menuButton = new MenuButton("search by NAME", null, skuSelectMenuItem, nameSelectMenuItem);
        menuButton.setPrefWidth(120);

        measSearchMode = SalesImputer.NAME;
        skuSelectMenuItem.setOnAction(a ->{
            menuButton.setText("search by SKU");
            searchBar.setPromptText("search item by SKU");
            measSearchMode = SalesImputer.SKU;
        });
        nameSelectMenuItem.setOnAction(a ->{
            menuButton.setText("search by NAME");
            searchBar.setPromptText("search item by NAME");
            measSearchMode = SalesImputer.NAME;
        });


        Button editButton = new Button("edit it");
        HBox searchHBox = new HBox(menuButton, searchBar, editButton);

        Text skuHeaderText = new Text("SKU");
        skuHeaderText.setWrappingWidth(117);
        Text nameHeaderText = new Text(SalesImputer.NAME);
        nameHeaderText.setWrappingWidth(550);
        Text rateHeader = new Text("RATE");
        rateHeader.setWrappingWidth(50);
        Text idHeader = new Text("ID");
        idHeader.setWrappingWidth(100);
        Text ruleHeader = new Text("RULE");
        ruleHeader.setWrappingWidth(50);
        Text copyHeader = new Text("COPY SKU");
        copyHeader.setWrappingWidth(80);

        HBox headerHBox = new HBox(skuHeaderText, nameHeaderText, rateHeader, idHeader, ruleHeader, copyHeader);

        measListView = new ListView<Meas>();
        this.selectedMeasList = new ArrayList<Meas>();
        measListView.setCellFactory(new MeasCellFactory(selectedMeasList, measImputer));
        observableMeasList = FXCollections.observableArrayList(measImputer.getMeasList());
        refillMeasListView(measListView, measImputer.getMeasList());

        // measListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // measListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
        //     HBox hBox = observable.getValue();
        //     if(hBox != null){
        //         Text skuText = (Text) hBox.getChildren().get(0);
        //         this.selectedMeasSku = skuText.getText();
        //     }
        // });

        editButton.setOnAction(a ->{
            if(selectedMeasList.isEmpty() || selectedMeasList.get(0) == null) return;
            String relativeId = selectedMeasList.get(0).getRelativeId();
            
            ArrayList<Meas> measList = measImputer.getMeasList();

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
                this.changeButtonMode(MeasImputer.UPDATE);
                this.editMeas(meas);
            }
        });

        searchBar.textProperty().addListener((observable, oldValue, newValue) ->{
            ArrayList<Meas> matchedMeasList = new ArrayList<Meas>();
            String[] splitStr = searchBar.getText().split("\\s+");
            
            for(Meas meas : measImputer.getMeasList()){
                String matchStr = null;
                switch(this.measSearchMode){
                    case SalesImputer.NAME: 
                        if(meas.getName() == null) continue;
                        matchStr = meas.getName().toLowerCase();
                        break;
                    case SalesImputer.SKU:
                        if(meas.getRelativeId() == null) continue;
                        matchStr = meas.getRelativeId().toLowerCase();
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
                matchedMeasList = measImputer.getMeasList();
            }
            refillMeasListView(measListView, matchedMeasList);
        });

        return new VBox(searchHBox, headerHBox, measListView);
    }
    
    public void changeButtonMode(String mode){
        switch(mode){
            case MeasImputer.CREATE:
                this.buttonMode = MeasImputer.CREATE;
                if(this.createButton != null) this.createButton.setText(MeasImputer.CREATE);

                if(updateIdButton != null) updateIdButton.setDisable(true);
                break;
            case MeasImputer.UPDATE:
                this.buttonMode = MeasImputer.UPDATE;
                if(this.createButton != null) this.createButton.setText(MeasImputer.UPDATE);

                if(updateIdButton != null) updateIdButton.setDisable(false);
                break;
        }
    }

    public void editMeas(Meas meas){
        // selectedMeasList.clear();
        // selectedMeasList.add(meas);
        toEditMeas = meas;
        changeButtonMode(MeasImputer.UPDATE);
        this.productNameField.setText(meas.getName());
        this.measurementField.setText(Double.toString(meas.getMeasurement()));
        this.updateRuleField.setText(meas.getUpdateRule());
    }

    public VBox generatePanel(){
        Label productNameLabel = new Label("Product Name");
        this.productNameField = new TextField();
        productNameField.setPromptText("search item by product description");
        productNameField.setPrefWidth(200);
        Label measurementLabel = new Label("Measurement");
        measurementField = new TextField();
        measurementField.textProperty().addListener((observable, oldValue, newValue) ->{
            if(!newValue.matches("\\d*")){
                measurementField.setText(newValue.replaceAll("[^\\d.]", ""));
            }
        });
        measurementField.setPromptText("measure size default was 1.00");
        Label parentSkuLabel = new Label("parent Sku");
        parentSkuField = new TextField();
        parentSkuField.setTooltip(new Tooltip("connect meas with parent sku id as child sku"));
        parentSkuField.setPromptText("grouping with same sku");
        Label updateRuleLabel = new Label("Update Rule");
        updateRuleField = new TextField();
        updateRuleField.setPromptText("default was 3t");

        createButton = new Button(MeasImputer.CREATE);
        createButton.setPrefWidth(300);
        changeButtonMode(MeasImputer.CREATE);
        createButton.setOnAction(a-> {

            switch(this.buttonMode){
                case MeasImputer.CREATE:
                    String parentSku = null;
                    if(this.selectedProductId == null || this.selectedProductId.isEmpty()) return;
                    if(measurementField.getText() == null || measurementField.getText().isEmpty()) return;

                    Meas meas = new Meas();

                    meas.setId(this.selectedProductId);
                    meas.setMeasurement(Double.parseDouble(measurementField.getText()));
                    if(parentSkuField.getText() != null || !parentSkuField.getText().isEmpty()) parentSku = parentSkuField.getText();
                    if(updateRuleField.getText() != null || !parentSkuField.getText().isEmpty()) meas.setUpdateRule(updateRuleField.getText());

                    if(parentSku != null && !parentSku.isEmpty()){
                        meas.setRelativeId(measImputer.createNewChildSku(parentSku));
                    } else {
                        meas.setRelativeId(measImputer.createNewSku());
                    }
                    
                    measImputer.getMeasList().add(meas);
                    measImputer.imputeNameField(measImputer.getMeasList());
                    measImputer.setMeasChange(true);

                    refeshListView();
                    break;
                case MeasImputer.UPDATE:
                    
                    if(toEditMeas == null 
                        || measurementField.getText() == null || measurementField.getText().isEmpty()) return;

                    toEditMeas.setUpdateRule(updateRuleField.getText());
                    toEditMeas.setMeasurement(Double.parseDouble(measurementField.getText()));
                    changeButtonMode(MeasImputer.CREATE);
                    
                    measImputer.setMeasChange(true);

                    refeshListView();
                    break;
                default:
                    break;
            }

            this.productNameField.clear();
            this.measurementField.clear();
            this.updateRuleField.clear();
            this.parentSkuField.clear();
            this.toEditMeas = null;
            selectedProductId = null;
        });


        return new VBox(productNameLabel, productNameField, measurementLabel, 
            measurementField, parentSkuLabel, parentSkuField, updateRuleLabel, updateRuleField, createButton,
            generatedUOMListView());
    }

    public MeasImputingController(){
        this.measImputer = new MeasImputer();
    }

    public String getSelectedMeasSku() {
        if(selectedMeasList.isEmpty() || selectedMeasList.get(0) == null) return null;
        return selectedMeasList.get(0).getRelativeId();
    }

    public void close() {
        if(measImputer != null && measImputer.isMeasChanged()) measImputer.saveChange();
        new Alert(AlertType.INFORMATION, "Measurement Mapping is saved", ButtonType.OK).show();
    }

    public Stage initDialog(Stage stage) {
        HBox hBox = new HBox(this.generateMeasListViewPanel(), this.generatePanel());

        stage.setScene(new Scene(hBox));
        stage.setTitle("Measurement Editor");

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                if(measImputer != null && measImputer.isMeasChanged()) measImputer.saveChange();
            }
        });

        return stage;
    }
}
