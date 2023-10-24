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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.UOM;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MeasImputer {
  // TODO : separate uoms with observableList, also meas

    private static final String CREATE = "CEATE";
    public static final String UPDATE = "UPDATE";
    private List<UOM> irsUoms;
    private TextField productNameField;
    private String selectedProductId;
    private ArrayList<Meas> measList;
    private String buttonMode;
    private Meas selectMeas;
    private Button createButton;
    private TextField measurementField;
    private TextField updateRuleField;
    private boolean measChanged = false;

    public boolean isMeasChanged() {
        return measChanged;
    }

    public ArrayList<Meas> getMeasList() {
        return measList;
    }

    public void imputeNameField(ArrayList<Meas> measList) {

        List<UOM> irsUoms = ShopeeSalesConvertApplication.getIrsUoms();

        measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
                return o1.getId().toLowerCase().compareTo(o2.getId().toLowerCase());
            }
            
        });

        irsUoms.removeIf(uom -> (uom.getRate() != 1));

        irsUoms.sort(new Comparator<UOM>() {

            @Override
            public int compare(UOM o1, UOM o2) {
                return o1.getProductId().toLowerCase().compareTo(o2.getProductId().toLowerCase());
            }
            
        });

        for(Meas meas : measList){
            UOM uom = binarySearch(meas, irsUoms);
            if(uom == null) continue;
            meas.setName(uom.getDescription());
        }
    }

    private UOM binarySearch(Meas meas, List<UOM> uoms){
        
        int lo = 0;
        int hi = uoms.size()-1;

        while(lo <= hi) {
            int mid = lo + (hi-lo) / 2;
            if(uoms.get(mid).getProductId().toLowerCase().compareTo(meas.getId().toLowerCase()) > 0) hi = mid-1; 
            else if(uoms.get(mid).getProductId().toLowerCase().compareTo(meas.getId().toLowerCase()) < 0) lo = mid+1;
            else{
                return uoms.get(mid);
            }
        }
        return null;
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
        TextField parentSkuField = new TextField();
        parentSkuField.setTooltip(new Tooltip("connect meas with parent sku id as child sku"));
        parentSkuField.setPromptText("grouping with same sku");
        Label updateRuleLabel = new Label("Update Rule");
        updateRuleField = new TextField();
        updateRuleField.setPromptText("default was 3t");

        createButton = new Button("create");
        createButton.setPrefWidth(300);
        changeButtonMode(CREATE);
        createButton.setOnAction(a-> {

            switch(this.buttonMode){
                case CREATE:
                    String parentSku = null;
                    if(this.selectedProductId == null || this.selectedProductId.isEmpty()) return;
                    if(measurementField.getText() == null || measurementField.getText().isEmpty()) return;

                    Meas meas = new Meas();

                    meas.setId(this.selectedProductId);
                    meas.setMeasurement(Double.parseDouble(measurementField.getText()));
                    if(parentSkuField.getText() != null || !parentSkuField.getText().isEmpty()) parentSku = parentSkuField.getText();
                    if(updateRuleField.getText() != null || !parentSkuField.getText().isEmpty()) meas.setUpdateRule(updateRuleField.getText());

                    if(parentSku != null && !parentSku.isEmpty()){
                        meas.setRelativeId(createNewChildSku(parentSku));
                    } else {
                        meas.setRelativeId(createNewSku());
                    }
                    
                    measList.add(meas);
                    this.imputeNameField(measList);
                    this.measChanged = true;
                    break;
                case UPDATE:
                    
                    if(this.selectMeas == null|| measurementField.getText() == null || measurementField.getText().isEmpty()) return;

                    this.selectMeas.setUpdateRule(updateRuleField.getText());
                    this.selectMeas.setMeasurement(Double.parseDouble(measurementField.getText()));
                    changeButtonMode(CREATE);
                    
                    this.measChanged = true;
                    break;
                default:
                    break;
            }

            this.productNameField.clear();
            this.measurementField.clear();
            this.updateRuleField.clear();
            parentSkuField.clear();
            selectMeas = null;
            selectedProductId = null;
        });


        return new VBox(productNameLabel, productNameField, measurementLabel, 
            measurementField, parentSkuLabel, parentSkuField, updateRuleLabel, updateRuleField, createButton,
            generatedUOMListView());
    }

    public void changeButtonMode(String mode){
        switch(mode){
            case CREATE:
                this.buttonMode = CREATE;
                if(this.createButton != null) this.createButton.setText(CREATE);
                break;
            case UPDATE:
                this.buttonMode = UPDATE;
                if(this.createButton != null) this.createButton.setText(UPDATE);
                break;
        }
    }

    public void editMeas(Meas meas){
        this.selectMeas = meas;
        changeButtonMode(UPDATE);
        this.productNameField.setText(meas.getName());
        this.measurementField.setText(Double.toString(meas.getMeasurement()));
        this.updateRuleField.setText(meas.getUpdateRule());
    }

    private String createNewSku() {
        String lastSku = measList.get(measList.size()-1).getRelativeId();
        lastSku = lastSku.split("-")[0];
        int parseInt = Integer.parseInt(lastSku);
        parseInt++;
        return Integer.toString(parseInt);
    }

    public String createNewChildSku(String parentSkuString) {
        measList.sort(new Comparator<Meas>(){
            @Override
            public int compare(Meas o1, Meas o2) {
                return o1.getRelativeId().compareTo(o2.getRelativeId());
            }
        });

        String parentOriginSku = parentSkuString.contains("-")? parentSkuString.split("-")[0] : parentSkuString;
        String parentAnotherChildSku = parentOriginSku + "-a";

        
        boolean foundIt = false;
        int mid = 0;
        int lo = 0;
        int hi = measList.size()-1;
        while(lo <= hi){
            mid = lo + (hi-lo) / 2;
            if(measList.get(mid).getRelativeId().toLowerCase().compareTo(parentOriginSku.toLowerCase()) > 0) hi = mid-1;
            else if(measList.get(mid).getRelativeId().toLowerCase().compareTo(parentOriginSku.toLowerCase()) < 0) lo = mid+1;
            else {
                foundIt = true;
                break;
            }
        }

        if(foundIt){
            measList.get(mid).setRelativeId(parentOriginSku + "-a");
            return parentOriginSku + "-b";
        }

        // find with parentAnotherChildSku
        if(foundIt == false){
            lo = 0;
            hi = measList.size()-1;

            while(lo <= hi){
                mid = lo + (hi-lo) / 2;
                if(measList.get(mid).getRelativeId().toLowerCase().compareTo(parentAnotherChildSku.toLowerCase()) > 0) hi = mid-1;
                else if(measList.get(mid).getRelativeId().toLowerCase().compareTo(parentAnotherChildSku.toLowerCase()) < 0) lo = mid+1;
                else {
                    foundIt = true;
                    break;
                }
            }
        }

        int last = mid;
        int letter = 97; // ASCII for 'b';
        int prefixLetter = 0;
        for( ; measList.get(last).getRelativeId().contains(parentOriginSku); last++){
            letter++;
            if(letter > 122){
                letter = 97;
                prefixLetter = 90;
            }
        }

        if(prefixLetter == 0){
            return parentOriginSku + "-" + ((char) letter);
        }
        return parentOriginSku + "-" + ((char) prefixLetter) + ((char) letter);
    }

    public VBox generatedUOMListView(){
        ListView<HBox> uomListView = new ListView<HBox>();
        uomListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        if(this.irsUoms == null) this.irsUoms = ShopeeSalesConvertApplication.getIrsUoms();

        for(UOM uom : irsUoms){
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
            
            for(UOM uom : this.irsUoms){
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
                matchedUoms = new ArrayList<UOM>(this.irsUoms);
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

        return new VBox(new HBox(searchBar, useButton), uomListView);
        
    }

    public void saveChange(){

        measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
                return o1.getRelativeId().toLowerCase().compareTo(o2.getRelativeId().toLowerCase());
            }
            
        });
        
        String measPath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MEAS);
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(measPath));
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            int startRow = 1;
            
            for(Meas meas : measList){
                int rowIndex = startRow++;
                Row row = sheet.getRow(rowIndex);

                if(row == null) row = sheet.createRow(rowIndex);

                Cell skuCell = row.getCell(0);
                if(skuCell == null) skuCell = row.createCell(0);

                Cell idCell = row.getCell(1);
                if(idCell == null) idCell = row.createCell(1);

                Cell measurementCell = row.getCell(2);
                if(measurementCell == null) measurementCell = row.createCell(2);

                Cell updateRuleCell = row.getCell(3);
                if(updateRuleCell == null) updateRuleCell = row.createCell(3);
                
                skuCell.setCellValue(meas.getRelativeId());
                idCell.setCellValue(meas.getId());
                measurementCell.setCellValue(meas.getMeasurement());
                updateRuleCell.setCellValue(meas.getUpdateRule());
            }

            fileInputStream.close();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(measPath));
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

    public MeasImputer(){
        this.measList = ShopeeSalesConvertApplication.getMeasList();
    }
    
}
