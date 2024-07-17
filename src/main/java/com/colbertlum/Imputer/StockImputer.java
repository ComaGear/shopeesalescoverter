package com.colbertlum.Imputer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.Exception.OnlineSalesInfoException;
import com.colbertlum.contentHandler.OnlineSalesInfoContentHandler;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.OnlineSalesInfoReason;
import com.colbertlum.entity.ProductStock;

public class StockImputer {

    /**
     *
     */
    public static final String EMPTY_SKU = "empty sku";
    private static final String DEFAULT = "default";
    public static final String MANUAL_SET_STOCK_STATUS = "manual set stock";
    public static final String NOT_EXIST_PRODUCT_ID_STATUS = "not exist product id";
    public static final String NOT_EXIST_SKU_STATUS = "not exist sku";
    private static final String SELF_MANUAL_INPUT = "self";
    private static final String COMMA_DELIMITER = ",";
    private Map<String, Double> updateRuleMap;
    private List<ProductStock> productStocks;
    private List<Meas> measList;
    private List<OnlineSalesInfoReason> infoStatusList;

    // public List<OnlineSalesInfoStatus> filterNotValidAndRemove(List<OnlineSalesInfo> onlineSalesInfos){
    //     if(infoStatusList == null) infoStatusList = new ArrayList<OnlineSalesInfoStatus>();

    //     for(OnlineSalesInfo info : onlineSalesInfos){
    //         String sku = info.getParentSku();
    //         if(sku == null || sku.isEmpty()){
    //             infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("empty sku"));
    //             continue;
    //             // may be just ignoring it.
    //         }

    //         Meas meas = getMeas(sku);
    //         if(meas == null){
    //             infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("not exist sku"));
    //             continue;
    //         }

    //         ProductStock productStock = getProductStock(meas.getId());
    //         if(productStock == null && meas.getUpdateRule() != "disc"){
    //             infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("not exist product id"));
    //             continue;
    //         }

    //         if(meas.getUpdateRule() != null && meas.getUpdateRule().equals(SELF_MANUAL_INPUT)){
                
    //         }
    //     }

    //     return infoStatusList;
    // }

    public List<OnlineSalesInfo> figureStock(List<OnlineSalesInfo> onlineStocks) throws OnlineSalesInfoException{
        
        if(infoStatusList == null) infoStatusList = new ArrayList<OnlineSalesInfoReason>();
        
        for(OnlineSalesInfo info : onlineStocks){

            String sku = info.getSku();
            if(sku == null) sku = info.getParentSku();
            if(sku == null || sku.isEmpty()){
                infoStatusList.add(new OnlineSalesInfoReason().setOnlineSalesInfo(info).setStatus(EMPTY_SKU));
                continue;
                // may be just ignoring it.
            }

            Meas meas = getMeas(sku);
            if(meas == null){
                infoStatusList.add(new OnlineSalesInfoReason().setOnlineSalesInfo(info).setStatus(NOT_EXIST_SKU_STATUS));
                continue;
            }

            ProductStock productStock = getProductStock(meas.getId());
            if(productStock == null && meas.getUpdateRule() != "disc"){
                infoStatusList.add(new OnlineSalesInfoReason().setOnlineSalesInfo(info).setStatus(NOT_EXIST_PRODUCT_ID_STATUS));
                continue;
            }
            
            double updateRuleDouble = 1d;
            if(meas.getUpdateRule() != null && meas.getUpdateRule().equals(SELF_MANUAL_INPUT)){
                infoStatusList.add(new OnlineSalesInfoReason().setOnlineSalesInfo(info).setStatus(MANUAL_SET_STOCK_STATUS));
                continue;
            }
            try {
                if(meas.getUpdateRule() == null) updateRuleDouble = getUpdateRuleMeasure(DEFAULT);
                else updateRuleDouble = getUpdateRuleMeasure(meas.getUpdateRule());
            } catch (Throwable e) {
                updateRuleDouble = 0.5d;
            }
            double availableStock = (productStock.getAvailableStock() / meas.getMeasurement()) * updateRuleDouble;
            if(availableStock > 0) {
                int floor = (int) Math.floor(availableStock);
                info.setQuantity(floor);
            }else {
                info.setQuantity(0);
            }
        }

        if(!infoStatusList.isEmpty()){
            throw new OnlineSalesInfoException(infoStatusList);
        }

        return onlineStocks;
    }

    public List<OnlineSalesInfo> getOnlineSalesInfoList(File file) throws IOException{
        ArrayList<OnlineSalesInfo> onlineSalesInfoList = new ArrayList<OnlineSalesInfo>();
        try {
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            OnlineSalesInfoContentHandler contentHandler = new OnlineSalesInfoContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(), onlineSalesInfoList);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (InvalidFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return onlineSalesInfoList;
    }

    public void updateOnlineSalesInfo(OnlineSalesInfo info, List<OnlineSalesInfo> infoList){
        
        infoList.sort(new Comparator<OnlineSalesInfo>() {

            @Override
            public int compare(OnlineSalesInfo o1, OnlineSalesInfo o2) {
                int compareTo = o1.getProductId().compareTo(o2.getProductId());
                if(compareTo == 0){
                    return o1.getVariationId().compareTo(o2.getVariationId());
                }
                return compareTo;
            }
        });

        int lo = 0;
        int hi = measList.size()-1;
        OnlineSalesInfo foundInfo = null;

        while(lo <= hi){
            int mid = lo + (hi - lo) / 2;
            if(infoList.get(mid).getProductId().compareTo(info.getProductId()) > 0) hi = mid-1;
            else if(infoList.get(mid).getProductId().compareTo(info.getProductId()) < 0) lo = mid+1;
            else {
                if(infoList.get(mid).getVariationId().compareTo(info.getVariationId()) > 0) hi = mid-1;
                else if(infoList.get(mid).getVariationId().compareTo(info.getVariationId()) < 0) lo = mid+1;
                foundInfo = infoList.get(mid);
                break;
            }
        }

        if(foundInfo != null){
            foundInfo.setQuantity(info.getQuantity());
            foundInfo.setParentSku(info.getParentSku());
            String sku = info.getSku();
            if(sku != null && !sku.isEmpty() && sku.contains("-")){
                foundInfo.setSku(sku);
            } else {
                foundInfo.setParentSku(sku);
            }
            foundInfo.setPrice(info.getPrice());
        }
    }

    public static void saveOutputToFile(List<OnlineSalesInfo> infoList, File file) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        // Workbook workbook = WorkbookFactory.create(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        for(OnlineSalesInfo info : infoList){
            int foundRow = info.getFoundRow();
            Row row = sheet.getRow(foundRow);
            Cell cell = row.getCell(0);
            if(cell == null || !cell.getStringCellValue().equals(info.getProductId())
                || row.getCell(2) == null || !row.getCell(2).getStringCellValue().equals(info.getVariationId())){
                return;
            }
            if(info.getParentSku() != null){
                Cell parentSKuCell = row.getCell(4);
                if(parentSKuCell == null) parentSKuCell = row.createCell(4);
                parentSKuCell.setCellValue(info.getParentSku());
            } 
            if(info.getSku() != null){
                Cell skuCell = row.getCell(5);
                if(skuCell == null) skuCell = row.createCell(5);
                skuCell.setCellValue(info.getSku());
            }
            Cell priceCell = row.getCell(6);
            if(priceCell == null) priceCell = row.createCell(6);
            priceCell.setCellValue(info.getPrice());

            Cell stockCell = row.getCell(7);
            if(stockCell == null) stockCell = row.createCell(7);
            stockCell.setCellValue(Double.valueOf(info.getQuantity()));
        }

        fileInputStream.close();

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        workbook.write(fileOutputStream);
        workbook.close();
        fileOutputStream.close();
    }

    public Double getUpdateRuleMeasure(String updateRule) throws Throwable{
        if(updateRuleMap == null || !updateRuleMap.containsKey(updateRule)) {
            retrieveUpdateRule();
            if(!updateRuleMap.containsKey(updateRule)) throw new Throwable("this update rule not exist in updateRule.csv : " + updateRule);
        }
        return updateRuleMap.get(updateRule);
    }

    private void retrieveUpdateRule(){
        
        if(this.updateRuleMap == null) updateRuleMap = new HashMap<String, Double>();

        // URL resource = getClass().getResource("classpath:/resource/updateRule.csv");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("./updateRule.csv");
        } catch (FileNotFoundException e){
            inputStream = ClassLoader.getSystemResourceAsStream("updateRule.csv");
        }
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = bufferedReader.readLine()) != null){
                String[] values = line.split(COMMA_DELIMITER);
                updateRuleMap.put(values[0], Double.parseDouble(values[1]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ProductStock getProductStock(String id){
        int lo = 0;
        int hi = productStocks.size()-1;
        
        while(lo <= hi){
            int mid = lo + (hi - lo) / 2;
            if(productStocks.get(mid).getId().toLowerCase().compareTo(id.toLowerCase()) > 0) hi = mid-1;
            else if(productStocks.get(mid).getId().toLowerCase().compareTo(id.toLowerCase()) < 0) lo = mid+1;
            else return productStocks.get(mid);
        }
        return null;
    }

    private Meas getMeas(String sku){
        sku = sku.toLowerCase();
        int lo = 0;
        int hi = measList.size()-1;
        
        while(lo <= hi){
            int mid = lo + (hi - lo) / 2;
            if(measList.get(mid).getRelativeId().toLowerCase().compareTo(sku) > 0) hi = mid-1;
            else if(measList.get(mid).getRelativeId().toLowerCase().compareTo(sku) < 0) lo = mid+1;
            else return measList.get(mid);
        }
        return null;
    }

    public StockImputer(List<ProductStock> productStocks, List<Meas> measList){
        this.productStocks = productStocks;
        this.measList = measList;

    }
}
