package com.colbertlum.contentHandler;

import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

public class ManualReservingStockByBiztoryFormattedContentHandler extends ContentHandler {

    private static final String CODE = "Barcode";
    private static final String QTY = "Quantity";
    private String id;
    private Double quantity;

    private Map<String, Double> reservingStockMap;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case CODE:
                this.id = value;
                break;
        
            case QTY:
                this.quantity = Double.parseDouble(value);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(id != null && !id.isEmpty()) {
            if(reservingStockMap.containsKey(id)) {
                reservingStockMap.put(id, reservingStockMap.get(id) + quantity);
            } else {
                reservingStockMap.put(id, quantity);
            }
        }
    }

    public ManualReservingStockByBiztoryFormattedContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, Map<String, Double> reservingStockMap) {
        super(sharedStrings, stylesTable);

        this.reservingStockMap = reservingStockMap;
    }
    
}
