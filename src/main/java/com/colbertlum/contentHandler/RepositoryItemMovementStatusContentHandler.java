package com.colbertlum.contentHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.ItemMovementStatus;

public class RepositoryItemMovementStatusContentHandler extends ContentHandler{

    private static final String ORDER_ID = "Order ID";
    private static final String SKU = "SKU";
    private static final String QUANTITY = "Quantity";
    private static final String TRACKING_NUMBER = "Tracking Number";
    private static final String STATUS = "Status";
    private static final String RECEIVED_QUANTITY = "Received Quantity";

    private ItemMovementStatus itemMovementStatus;
    private Map<String, List<ItemMovementStatus>> itemMap;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case ORDER_ID:
                itemMovementStatus.setOrderId(value);
                break;
            case SKU:
                itemMovementStatus.setSku(value);
                break;
            case QUANTITY:
                itemMovementStatus.setQuantity(Double.parseDouble(value));
                break;
            case TRACKING_NUMBER:
                itemMovementStatus.setTrackingNumber(value);
                break;
            case STATUS:
                itemMovementStatus.setStatus(value);
                break;
            case RECEIVED_QUANTITY:
                itemMovementStatus.setReceivedQuantity(Double.parseDouble(value));
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(itemMap.containsKey(itemMovementStatus.getOrderId())){
            itemMap.get(itemMovementStatus.getOrderId()).add(itemMovementStatus);
        } else {
            ArrayList<ItemMovementStatus> itemList = new ArrayList<ItemMovementStatus>();
            itemList.add(itemMovementStatus);
            itemMap.put(itemMovementStatus.getOrderId(), itemList);
        }
        itemMovementStatus = new ItemMovementStatus();
    }

    public RepositoryItemMovementStatusContentHandler(SharedStrings sharedStringsTable, StylesTable stylesTable,
            Map<String, List<ItemMovementStatus>> itemMap) {
        super(sharedStringsTable, stylesTable);
        this.itemMap = itemMap;
    }
    
    
}
