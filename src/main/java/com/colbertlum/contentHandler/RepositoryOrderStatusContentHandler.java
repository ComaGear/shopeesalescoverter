package com.colbertlum.contentHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.Imputer.OrderTrackingImputer;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.OrderStatusTracking;

public class RepositoryOrderStatusContentHandler extends ContentHandler {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private static final String ORDER_ID = "Order ID";
    private static final String STATUS = "Status";
    private static final String TRACKING_NUMBER = "Tracking Number";
    private static final String SHIPPING_TIME = "Shipping Time";
    private static final String COMPLETE_TIME = "Complete Time";

    private List<Order> orders;

    private Order order;
    @Override
    protected void onRow(int row) {
        if(statusTracking.getInternalStatus().equals(OrderTrackingImputer.STATUS_CANCELLED)){
            trackingsMap.get(OrderTrackingImputer.STATUS_CANCELLED).add(statusTracking);

        } else if(statusTracking.getInternalStatus().equals(OrderTrackingImputer.STATUS_COMPLETED)) {
            trackingsMap.get(OrderTrackingImputer.STATUS_COMPLETED).add(statusTracking);

        } else if(statusTracking.getInternalStatus().equals(OrderTrackingImputer.STATUS_SHIPPING)) {
            trackingsMap.get(OrderTrackingImputer.STATUS_SHIPPING).add(statusTracking);

        } else if(statusTracking.getInternalStatus().equals(OrderTrackingImputer.STATUS_RETURNING)) {
            trackingsMap.get(OrderTrackingImputer.STATUS_RETURNING).add(statusTracking);

        }

        order = new Order();
    }

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case ORDER_ID:
                statusTracking.setOrderId(value);
                break;
            case STATUS:
                statusTracking.setInternalStatus(value);
                break;
            case TRACKING_NUMBER:
                statusTracking.setTrackingNumber(value);
                break;
            case COMPLETE_TIME:
                statusTracking.setOrderCompletedTime(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case SHIPPING_TIME:
                statusTracking.setShipOutDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            default:
                break;
        }
    }

    public RepositoryOrderStatusContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,  List<Order> orders) {
        super(sharedStrings, stylesTable);
        this.order = new Order();
        this.orders = orders;
    }
    
}
