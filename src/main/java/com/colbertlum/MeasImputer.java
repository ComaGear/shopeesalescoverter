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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.UOM;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MeasImputer {
  // TODO : separate uoms with observableList, also meas

    public static final String CREATE = "CREATE";
    public static final String UPDATE = "UPDATE";
    private List<UOM> irsUoms;
    private ArrayList<Meas> measList;
    private boolean measChanged = false;
    
    public boolean isMeasChanged() {
        return measChanged;
    }

    public void setMeasChange(boolean b){
        this.measChanged = b;
    }

    public List<UOM> getIrsUoms() {
        if(irsUoms == null) irsUoms = ShopeeSalesConvertApplication.getIrsUoms();
        return irsUoms;
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

    public String createNewSku() {
        measList.sort(new Comparator<Meas>() {

            @Override
            public int compare(Meas o1, Meas o2) {
               return o1.getRelativeId().compareTo(o2.getRelativeId());
            }
            
        });
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

        String parentOriginSku = parentSkuString.contains("-") ? parentSkuString.split("-")[0] : parentSkuString;
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
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            // Workbook workbook = WorkbookFactory.create(fileInputStream);
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
        this.imputeNameField(measList);
    }
    
}
