package com.colbertlum.contentHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.Order;

public class RepositoryOrderStatusContentHandler extends ContentHandler {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private static final String ORDER_ID = "Order ID";
    private static final String TRACKING_NUMBER = "Tracking Number";
    private static final String CREATION_DATE = "Creation Date";
    private static final String SHIP_OUT_DATE = "ShipOut Date";
    private static final String COMPLETED_DATE = "Completed Date";
    private static final String REQUEST_RETURN_REFUND = "Request Return/Refund";
    private static final String STATUS = "Status";
    private static final String ORDER_TOTAL_AMOUNT = "Order Total Amount";
    private static final String MANAGEMENT_FEE = "Management Fee";
    private static final String TRANSACTION_FEE = "Transaction Fee";
    private static final String SERVICE_FEE = "Service Fee";
    private static final String COMMISION_FEE = "Commission Fee";
    private static final String SHOPEE_VOUCHER = "Shopee Voucher";
    private static final String SHIPPING_FEE = "Shipping Fee";
    private static final String SHIPPING_REBATE = "Shipping Rebate";
    


    private List<Order> orders;

    private Order order;
    @Override
    protected void onRow(int row) {

        if(order.getId() != null){
            orders.add(order);
        }
        order = new Order();
    }

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case ORDER_ID:
                order.setId(value);
                break;
            case TRACKING_NUMBER:
                order.setTrackingNumber(value);
                break;
            case CREATION_DATE:
                order.setOrderCreationDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case SHIP_OUT_DATE:
                order.setShipOutDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case COMPLETED_DATE:
                order.setOrderCompleteDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case REQUEST_RETURN_REFUND:
                order.setRequestApproved(value.equals("Request Approved"));
                break;
            case STATUS:
                order.setStatus(value);
                break;
            case ORDER_TOTAL_AMOUNT:
                order.setOrderTotalAmount(Double.parseDouble(value));
                break;
            case MANAGEMENT_FEE:
                order.setManagementFee(Double.parseDouble(value));
                break;
            case TRANSACTION_FEE:
                order.setTransactionFee(Double.parseDouble(value));
                break;
            case SERVICE_FEE:
                order.setServiceFee(Double.parseDouble(value));
                break;
            case COMMISION_FEE:
                order.setCommissionFee(Double.parseDouble(value));
                break;
            case SHOPEE_VOUCHER:
                order.setShopeeVoucher(Double.parseDouble(value));
                break;
            case SHIPPING_FEE:
                order.setShippingFee(Double.parseDouble(value));
                break;
            case SHIPPING_REBATE:
                order.setShippingRebateEstimate(Double.parseDouble(value));
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
