package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.Columns.RepositoryOrderColumn;
import com.colbertlum.entity.Order;

public class RepositoryOrderContentHandler extends ContentHandler {

    private List<Order> orders;
    private Order order;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case RepositoryOrderColumn.ORDER_ID:
                order.setId(value);
                break;
            case RepositoryOrderColumn.CREATION_DATE:
                order.setOrderCreationDate(DateTimePattern.getLocalDate(value));
                break;
            case RepositoryOrderColumn.SHIP_OUT_DATE:
                order.setShipOutDate(DateTimePattern.getLocalDate(value));
                break;
            case RepositoryOrderColumn.COMPLETED_DATE:
                order.setOrderCompleteDate(DateTimePattern.getLocalDate(value));
                break;
            case RepositoryOrderColumn.SETTLED_DATE:
                order.setSettledDate(DateTimePattern.getLocalDate(value));
                break;
            case RepositoryOrderColumn.MANAGEMENT_FEE:
                order.setManagementFee(Double.parseDouble(value));
                break;
            case RepositoryOrderColumn.ADJUSTMENT_SHIPPING_FEE:
                order.setAdjustmentshipppingFee(Double.parseDouble(value));
                break;
            case RepositoryOrderColumn.SELLER_REBATE:
                order.setSellerRebate(Double.parseDouble(value));
                break;
            case RepositoryOrderColumn.PLATFORM_REBATE:
                order.setPlatformRebate(Double.parseDouble(value));
                break;
            case RepositoryOrderColumn.ORDER_TOTAL_AMOUNT:
                order.setOrderTotalAmount(Double.parseDouble(value));
                break;
            case RepositoryOrderColumn.INTERNAL_STATUS:
                order.setInternalStatus(value);
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(order != null && order.getId() != null) orders.add(order);
        this.order = new Order();
    }

    public RepositoryOrderContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, List<Order> orders) {
        super(sharedStrings, stylesTable);
        this.orders = orders;
        this.order = new Order();
    }
    
}
