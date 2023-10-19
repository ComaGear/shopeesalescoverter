package com.colbertlum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.colbertlum.entity.Meas;

public class StockImputer {

    /**
     *
     */
    private static final String COMMA_DELIMITER = ",";
    private Map<String, Double> updateRuleMap;

    public void figureStock(List<OnlineSalesInfo> onlineStocks){
        
    }

    private Double getUpdateRuleMeasure(String updateRule) throws Throwable{
        if(updateRuleMap == null || !updateRuleMap.containsKey(updateRule)) {
            retrieveUpdateRule();
            if(!updateRuleMap.containsKey(updateRule)) throw new Throwable("this update rule not exist in updateRule.csv : " + updateRule);
        }
        return updateRuleMap.get(updateRule);
    }

    private void retrieveUpdateRule(){
        
        if(this.updateRuleMap == null) updateRuleMap = new HashMap<String, Double>();

        URL resource = getClass().getResource("classpath:/resource/updateRule.csv");
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(resource.getFile())))){
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

    public StockImputer(List<ProductStock> productStocks, List<Meas> measList){

    }
}
