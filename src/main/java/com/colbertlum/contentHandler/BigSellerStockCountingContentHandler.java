package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.BigSellerStockCounting;

public class BigSellerStockCountingContentHandler extends ContentHandler{

    private static final String SKU_NAME = "SKU Name";
    private static final String TITLE = "Title";
    private static final String WAREHOUSE = "Warehouse";
    private static final String SHELF = "Shelf";
    private static final String AREA = "Area";
    private static final String ON_HAND = "On Hand";
    private static final String COUNT = "Count";
    private static final String NOTE = "Note";
    private static final String IMAGE_URL = "Image URL";

    private List<BigSellerStockCounting> stockCountings;
private BigSellerStockCounting stockCounting;
    
    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case SKU_NAME:
                stockCounting.setSku(value);
                break;
            case TITLE:
                stockCounting.setName(value);
                break;
            case WAREHOUSE:
                stockCounting.setWarehouse(value);
                break;
            case SHELF:
                stockCounting.setShelf(value);
                break;
            case ON_HAND:
                stockCounting.setOnHand(Integer.parseInt(value));
                break;
            case COUNT:
                stockCounting.setStock(0);
                break;
            case NOTE:
                stockCounting.setNote(value);
                break;
            case IMAGE_URL:
                stockCounting.setImageUrl(value);
                break;
            case AREA:
                stockCounting.setArea(value);
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(stockCounting.getName() != null && stockCounting.getName() != "") {
            stockCountings.add(stockCounting);

            this.stockCounting = new BigSellerStockCounting();
        }
    }

    public BigSellerStockCountingContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, List<BigSellerStockCounting> stockCountings) {
        super(sharedStrings, stylesTable);
        this.stockCountings = stockCountings;
        this.stockCounting = new BigSellerStockCounting();
    }
    
}
