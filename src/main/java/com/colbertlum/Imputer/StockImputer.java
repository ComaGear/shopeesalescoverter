package com.colbertlum.Imputer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.colbertlum.Exception.ListingStockException;
import com.colbertlum.entity.Meas;
import com.colbertlum.entity.ListingStock;
import com.colbertlum.entity.ListingStockReason;
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
    private List<ListingStockReason> infoStatusList;
    
    public List<ListingStock> figureStock(List<ListingStock> listingStocks) throws ListingStockException{
        
        if(infoStatusList == null) infoStatusList = new ArrayList<ListingStockReason>();
        
        for(ListingStock info : listingStocks){

            String sku = info.getSku();
            if(sku == null || sku.isEmpty()){
                infoStatusList.add(new ListingStockReason().setOnlineSalesInfo(info).setStatus(EMPTY_SKU));
                continue;
            }

            Meas meas = getMeas(sku);
            if(meas == null){
                infoStatusList.add(new ListingStockReason().setOnlineSalesInfo(info).setStatus(NOT_EXIST_SKU_STATUS));
                continue;
            }

            ProductStock productStock = getProductStock(meas.getId());
            if(productStock == null && meas.getUpdateRule() != "disc"){
                infoStatusList.add(new ListingStockReason().setOnlineSalesInfo(info).setStatus(NOT_EXIST_PRODUCT_ID_STATUS));
                continue;
            }
            
            double updateRuleDouble = 1d;
            if(meas.getUpdateRule() != null && meas.getUpdateRule().equals(SELF_MANUAL_INPUT)){
                infoStatusList.add(new ListingStockReason().setOnlineSalesInfo(info).setStatus(MANUAL_SET_STOCK_STATUS));
                continue;
            }
            try {
                if(meas.getUpdateRule() == null) updateRuleDouble = getUpdateRuleMeasure(DEFAULT);
                else updateRuleDouble = getUpdateRuleMeasure(meas.getUpdateRule());
            } catch (Throwable e) {
                updateRuleDouble = 0.5d;
            }
            double availableStock = (productStock.getStock() / meas.getMeasurement()) * updateRuleDouble;
            if(availableStock > 0) {
                int floor = (int) Math.floor(availableStock);
                info.setStock(floor);
            }else {
                info.setStock(0);
            }
        }

        if(!infoStatusList.isEmpty()){
            throw new ListingStockException(infoStatusList);
        }

        return listingStocks;
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
