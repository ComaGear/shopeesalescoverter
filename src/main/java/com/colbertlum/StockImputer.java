package com.colbertlum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.colbertlum.entity.Meas;
import com.colbertlum.entity.OnlineSalesInfo;
import com.colbertlum.entity.ProductStock;

public class StockImputer {

    /**
     *
     */
    private static final String COMMA_DELIMITER = ",";
    private Map<String, Double> updateRuleMap;
    private List<ProductStock> productStocks;
    private List<Meas> measList;

    public void figureStock(List<OnlineSalesInfo> onlineStocks){
        
        for(OnlineSalesInfo info : onlineStocks){
            String sku = info.getSku();
            Meas meas = getMeas(sku);
            ProductStock productStock = getProductStock(meas.getId());
            double updateRuleDouble = 1d;
            try {
                updateRuleDouble = getUpdateRuleMeasure(meas.getUpdateRule());
            } catch (Throwable e) {
                updateRuleDouble = 0.5d;
            }
            double availableStock = (productStock.getAvailableStock() * meas.getMeasurement()) * updateRuleDouble;
            info.setQuantity((int) Math.floor(availableStock));
        }
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
        int hi = productStocks.size();
        
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
        int hi = measList.size();
        
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
