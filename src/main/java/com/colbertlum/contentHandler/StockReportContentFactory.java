package com.colbertlum.contentHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.colbertlum.ShopeeSalesConvertApplication;
import com.colbertlum.entity.ProductStock;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.Window;

public class StockReportContentFactory {


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
            e.printStackTrace();
        }

        return stocks;
    }

    public static Map<String, Double> getManualReservingStock(){

        String path = ShopeeSalesConvertApplication.getProperty(ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE);
        File file = new File(path);
        if(path == null || !file.isFile() || !file.exists()) {
            throw new NullPointerException(String.format("property %s is not valid", ShopeeSalesConvertApplication.MANUAL_RESERVING_STOCK_FILE));
        }

        Map<String, Double> manualReservingStockMap = new HashMap<String, Double>();

        try {
            XSSFReader xssfReader = new XSSFReader(OPCPackage.open(file));
            ManualReservingStockByBiztoryFormattedContentHandler contentHandler = new ManualReservingStockByBiztoryFormattedContentHandler(xssfReader.getSharedStringsTable(), xssfReader.getStylesTable(),
                manualReservingStockMap);
            XMLReader xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(contentHandler);
            InputSource sheetData = new InputSource(xssfReader.getSheetsData().next());
            xmlReader.parse(sheetData);
        } catch (IOException | OpenXML4JException | SAXException | ParserConfigurationException e) {
            if(Window.getWindows() != null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.show();
            } else {
                e.printStackTrace();
            }
        }
        return manualReservingStockMap;

    }

}
    
