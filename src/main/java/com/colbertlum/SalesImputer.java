package com.colbertlum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.colbertlum.Controller.MeasImputingController;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.MoveOutStatus;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class SalesImputer {

    static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String SKU = "SKU";


    private ArrayList<MoveOutStatus> moveOutStatusList;
    public ArrayList<MoveOutStatus> getMoveOutStatusList() {
        return moveOutStatusList;
    }

    // private MeasImputer measImputer;
    private boolean moveOutChanged;
    public boolean isMoveOutChanged() {
        return moveOutChanged;
    }

    private MeasImputingController measImputingController;

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


        // controller view
        if(measImputingController == null) this.measImputingController = new MeasImputingController();
    }

    // public MoveOutStatus getMoveOutFromStatusListByFoundRow(String foundRow) {
    //     if(moveOutStatusList == null) return null;
        
    //     for(MoveOutStatus moveOutStatus : moveOutStatusList){
    //         if(Integer.parseInt(foundRow) == moveOutStatus.getMoveOut().getFoundRow()){
    //             return moveOutStatus;
    //         }
    //     }
    //     return null;
    // }

    public void saveChange(){
        List<MoveOutStatus> resolveMoveOutStatus = moveOutStatusList;

        String sourcePath = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.REPORT);
        
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(sourcePath));
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            // Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            // validate column
            Cell skuHeaderCell;
            if(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.DATA_SOURCE_TYPE)
            .equals(ShopeeSalesConvertApplication.SHOPEE_ORDER)){
                skuHeaderCell = headerRow.getCell(13);
            } else{
                skuHeaderCell = headerRow.getCell(30);
            }
            String skuHeaderCellValue = skuHeaderCell.getStringCellValue();
            skuHeaderCellValue.replaceAll("[^a-zA-z0-9]", "");
            if(skuHeaderCell == null || 
                (!skuHeaderCellValue.equals("SKU") && !(skuHeaderCellValue.equals("SKU Reference No.")))) {
                new Alert(AlertType.ERROR, "SKU header is moved or select wrong sales file", ButtonType.OK).show();
            }

            int skuPosition = -1;
            if(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.DATA_SOURCE_TYPE)
            .equals(ShopeeSalesConvertApplication.SHOPEE_ORDER)){
                skuPosition = 13;
            } else if(ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.DATA_SOURCE_TYPE)
            .equals(ShopeeSalesConvertApplication.BIG_SELLER)){
                skuPosition = 30;
            } else {
                new Alert(AlertType.ERROR, "choose valid data source type", ButtonType.OK).showAndWait();
            }

            for(MoveOutStatus moveOutStatus : resolveMoveOutStatus){
                int foundRow = moveOutStatus.getMoveOut().getFoundRow();
                Row row = sheet.getRow(foundRow);
                Cell skuCell = row.getCell(skuPosition);
  
                if(skuCell == null) skuCell = row.createCell(skuPosition);
                skuCell.setCellValue(moveOutStatus.getMoveOut().getSku());
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

    // public void setMeasImputer(MeasImputer measImputer) {
    //     this.measImputer = measImputer;
    // }

    public void setMoveOutChanged(boolean b) {
        this.moveOutChanged = b;
    }
}
