package com.colbertlum.contentHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.ItemMovementStatus;
import com.colbertlum.entity.OrderStatusTracking;

public class SpecifyOrderMovementContentHandler extends ContentHandler {

    private static final String ORDER_ID = "Order ID";
    private static final String SKU = "SKU Reference No.";
    private static final String QUANTITY = "Quantity";
    private static final String TRACKING_NUMBER = "Tracking Number*";
    private static final String STATUS = "Order Status";
    private static final String PARENT_SKU = "Parent SKU Reference No.";

    private List<OrderStatusTracking> trackings;
    private ItemMovementStatus item;

    private int trackingsIndex = 0;
    private boolean hasSame = false;

    @Override
    protected void onRow(int row) {

        if(trackingsIndex >= trackings.size()) return;

        if(trackings.get(trackingsIndex).getOrderId().equals(item.getOrderId())){
            if(trackings.get(trackingsIndex).getItemMovementStatusList() == null) {
                trackings.get(trackingsIndex).setItemMovementStatusList(new ArrayList<ItemMovementStatus>());
            }
            trackings.get(trackingsIndex).getItemMovementStatusList().add(item);
            hasSame = true;
        } else if(hasSame){
            trackingsIndex++;
            if(trackingsIndex == trackings.size()) return;
            trackings.get(trackingsIndex).setItemMovementStatusList(new ArrayList<ItemMovementStatus>());
            hasSame = false;
        } else {

        }

        this.item = new ItemMovementStatus();
    }

    @Override
    protected void onCell(String header, int row, String value) {

        if(value == null || value.isEmpty()) return;

        switch (header) {
            case ORDER_ID:
                item.setOrderId(value);
                break;
            case SKU:
                item.setSku(value);
                break;
            case QUANTITY:
                item.setQuantity(Double.parseDouble(value));
                break;
            case PARENT_SKU:
                item.setParentSku(value);
                break;
            case TRACKING_NUMBER:
                item.setTrackingNumber(value);
                break;
            case STATUS:
                item.setStatus(value);
                break;
            default:
                break;
        }
    }

    public SpecifyOrderMovementContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, 
        List<OrderStatusTracking> trackings) {
        super(sharedStrings, stylesTable);
        this.trackings = trackings;

        item = new ItemMovementStatus();

        trackings.sort(new Comparator<OrderStatusTracking>() {

            @Override
            public int compare(OrderStatusTracking o1, OrderStatusTracking o2) {
                return o1.getOrderId().compareTo(o2.getOrderId());
            }
            
        });
    }
}
