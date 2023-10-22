package com.colbertlum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.colbertlum.Exception.OnlineSalesInfoException;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.OnlineSalesInfoStatus;
import com.colbertlum.entity.ProductStock;

public class StockImputer {

    /**
     *
     */
    private static final String SELF_MANUAL_INPUT = "self";
    private static final String COMMA_DELIMITER = ",";
    private Map<String, Double> updateRuleMap;
    private List<ProductStock> productStocks;
    private List<Meas> measList;
    private List<OnlineSalesInfoStatus> infoStatusList;

    public List<OnlineSalesInfo> figureStock(List<OnlineSalesInfo> onlineStocks) throws OnlineSalesInfoException{
        
        if(infoStatusList == null) infoStatusList = new ArrayList<OnlineSalesInfoStatus>();
        
        for(OnlineSalesInfo info : onlineStocks){

            String sku = info.getSku();
            if(sku == null) sku = info.getParentSku();
            if(sku == null || sku.isEmpty()){
                infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("empty sku"));
                continue;
            }

            Meas meas = getMeas(sku);
            if(meas == null){
                infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("not exist sku"));
                continue;
            }

            ProductStock productStock = getProductStock(meas.getId());
            if(productStock == null && meas.getUpdateRule() != "disc"){
                infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("not exist product id"));
                continue;
            }
            
            double updateRuleDouble = 1d;
            if(meas.getUpdateRule() != null && meas.getUpdateRule().equals(SELF_MANUAL_INPUT)){
                infoStatusList.add(new OnlineSalesInfoStatus().setOnlineSalesInfo(info).setStatus("manual set stock"));
                continue;
            }
            try {
                if(meas.getUpdateRule() == null) updateRuleDouble = getUpdateRuleMeasure("default");
                else updateRuleDouble = getUpdateRuleMeasure(meas.getUpdateRule());
            } catch (Throwable e) {
                updateRuleDouble = 0.5d;
            }
            String parentsku = info.getParentSku();
             String productId = info.getProductId();
            double availableStock = (productStock.getAvailableStock() / meas.getMeasurement()) * updateRuleDouble;
            if(availableStock > 0) {
                int floor = (int) Math.floor(availableStock);
                info.setQuantity(floor);
            }
            else info.setQuantity(0);
        }

        if(!infoStatusList.isEmpty()){
            throw new OnlineSalesInfoException(infoStatusList);
        }

        return onlineStocks;
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
            foundInfo.setSku(info.getSku());
            foundInfo.setPrice(info.getPrice());
        }
    }

    public static void saveOutputToFile(List<OnlineSalesInfo> infoList, File file) throws IOException{
        FileInputStream fileInputStream = new FileInputStream(file);
        Workbook workbook = WorkbookFactory.create(fileInputStream);
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
            stockCell.setCellValue(info.getQuantity());
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
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("updateRule.csv")))){
            String line;
            while((line = bufferedReader.readLine()) != null){
                String[] values = line.split(COMMA_DELIMITER);
                updateRuleMap.put(values[0], Double.parseDouble(values[1]));
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ProductStock getProductStock(String id){
        int lo = 0;
        int hi = productStocks.size()-1;
        
        while(lo <= hi){
            int mid = lo + (hi - lo) / 2;
            if(productStocks.get(mid).getId().toLowerCase().compareTo(id) > 0) hi = mid-1;
            else if(productStocks.get(mid).getId().toLowerCase().compareTo(id) < 0) lo = mid+1;
            else return productStocks.get(mid);
        }
        return null;
    }

    private Meas getMeas(String sku){
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
