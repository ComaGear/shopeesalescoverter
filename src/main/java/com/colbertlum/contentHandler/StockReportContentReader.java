package com.colbertlum.contentHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.entity.ProductStock;

public class StockReportContentReader {


    private static final String STOCK = "Stock";
    private static final String PRODUCT_CODE = "Product Code";
    private static final String COMMA_DELIMITER = ",";

    public static List<ProductStock> getStockReport() throws IOException{
        HashMap<Integer, String> headerMap = new HashMap<Integer, String>();
        ArrayList<ProductStock> stocks = new ArrayList<ProductStock>();

        String path = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.STOCK_REPORT_PATH);
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)))){
            String line;
            boolean readHeader = false;
            while((line = bufferedReader.readLine()) != null){
                String[] values = line.split(COMMA_DELIMITER);
                if(readHeader == false){
                    for(int i = 0; i < values.length; i++){
                        String head = values[i];
                        head = head.replaceAll("[^a-zA-Z0-9\\s]", "");
                        switch(head){
                            case PRODUCT_CODE:
                                headerMap.put(i, PRODUCT_CODE);
                                break;
                            case STOCK:
                                headerMap.put(i, STOCK);
                                break;
                        }
                    }
                    readHeader = true;
                    continue;
                }

                ProductStock productStock = new ProductStock();
                for(int i = 0; i < values.length; i++){
                    String column = "";
                    if(headerMap.containsKey(i)) column = headerMap.get(i);
                    
                    String value = values[i];
                    value = value.replaceAll("[^a-zA-Z0-9\\s.-]", "");
                    switch(column){
                        case PRODUCT_CODE:
                            productStock.setId(value);
                            break;
                        case STOCK:
                            productStock.setStock(Double.parseDouble(value));
                            break;
                    }
                    
                }
                if(productStock.getId() != null) stocks.add(productStock);
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return stocks;
    }
}
    
