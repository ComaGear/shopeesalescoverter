package com.colbertlum.contentHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.PlatformType;
import com.colbertlum.constants.Columns.RepositoryItemMovementColumn;
import com.colbertlum.constants.Columns.RepositoryOrderColumn;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ShopeeOrder;
import com.colbertlum.entity.TikTokOrder;

public class RepositoryOrderContentHandler extends ContentHandler {

    private List<Order> orders;
    private Order order;

    private Map<String, String> valueMap = new HashMap<String, String>();


    @Override
    protected void onCell(String header, int row, String value) {
        valueMap.put(header, value);
    }

    @Override
    protected void onRow(int row) {
        
        if(valueMap.get(RepositoryItemMovementColumn.PLATFORM).equals(PlatformType.SHOPEE)){
            ShopeeOrder shopeeOrder = new ShopeeOrder();
            
            this.order = shopeeOrder;
        } else if (valueMap.get(RepositoryItemMovementColumn.PLATFORM).equals(PlatformType.TIKTOK)) {
            TikTokOrder tikTokOrder = new TikTokOrder();
            this.order = tikTokOrder;
        }

        order.setId(valueMap.get(RepositoryOrderColumn.ORDER_ID));

        order.setCreationDate(DateTimePattern.getLocalDate(valueMap.get(RepositoryOrderColumn.CREATION_DATE)));
        order.setShipOutDate(DateTimePattern.getLocalDate(valueMap.get(RepositoryOrderColumn.SHIP_OUT_DATE)));
        order.setCompletedDate(DateTimePattern.getLocalDate(valueMap.get(RepositoryOrderColumn.COMPLETED_DATE)));
        order.setSettledDate(DateTimePattern.getLocalDate(valueMap.get(RepositoryOrderColumn.SETTLED_DATE)));

        order.setManagementFee(Double.parseDouble(valueMap.get(RepositoryOrderColumn.MANAGEMENT_FEE)));
        order.setAdjustmentshipppingFee(Double.parseDouble(valueMap.get(RepositoryOrderColumn.ADJUSTMENT_SHIPPING_FEE)));
        order.setSellerRebate(Double.parseDouble(valueMap.get(RepositoryOrderColumn.SELLER_REBATE)));
        order.setPlatformRebate(Double.parseDouble(valueMap.get(RepositoryOrderColumn.PLATFORM_REBATE)));
        order.setOrderTotalAmount(Double.parseDouble(valueMap.get(RepositoryOrderColumn.ORDER_TOTAL_AMOUNT)));

        order.setInternalStatus(valueMap.get(RepositoryOrderColumn.INTERNAL_STATUS));
        
        if(order != null && order.getId() != null) orders.add(order);
    }

    public RepositoryOrderContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, List<Order> orders) {
        super(sharedStrings, stylesTable);
        this.orders = orders;
        this.order = new Order();
    }
    
}
