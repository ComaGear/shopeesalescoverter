package com.colbertlum.contentHandler;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.Columns.OrderReportTikTokOrderColumn;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.TikTokMoveOut;
import com.colbertlum.entity.TikTokOrder;

public class TikTokOrderReportContentHandler extends ContentHandler {

    private List<MoveOut> moveOuts;
    private Map<String, TikTokOrder> orderMap;
    private TikTokMoveOut moveOut;
    private TikTokOrder order;
    
    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case OrderReportTikTokOrderColumn.ORDER_ID:
                order.setId(value);
                break;
            case OrderReportTikTokOrderColumn.SHIP_OUT_DATE:
                order.setShipOutDate(DateTimePattern.getLocalDate(value));
                break;
            case OrderReportTikTokOrderColumn.ORDER_CREATION_DATE:
                order.setOrderCreationDate(DateTimePattern.getLocalDate(value));
                break;
            case OrderReportTikTokOrderColumn.SKU:
                moveOut.setSku(value);
                break;
            case OrderReportTikTokOrderColumn.QUANTITY:
                moveOut.setQuantity(Double.parseDouble(value));
                break;
            case OrderReportTikTokOrderColumn.PRODUCT_NAME:
                moveOut.setProductName(value);
                break;
            case OrderReportTikTokOrderColumn.VARIATION_NAME:
                moveOut.setVariationName(value);
                break;
            case OrderReportTikTokOrderColumn.PRODUCT_SUBTOTAL:
                moveOut.setSKUsubtotalAfterDiscount(Double.parseDouble(value));
                break;
            case OrderReportTikTokOrderColumn.PLATFORM_SUBTOTAL_DISCOUNT:
                moveOut.setSKUplatformDiscount(Double.parseDouble(value));
                break;
            case OrderReportTikTokOrderColumn.TRACKING_NUMBER:
                order.setTrackingNumber(value);
                break;
            case OrderReportTikTokOrderColumn.CANCELATION_OR_RETURN_TYPE:
                if(value != null && !value.isEmpty() && value.equals(OrderReportTikTokOrderColumn.RETURN_REFUND_COLUMN_VALUE)){
                    order.setReturnRefund(true);
                }
                break;
            case OrderReportTikTokOrderColumn.ORDER_STATUS:
                order.setStatus(value);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(moveOut.getProductName() != null && moveOut.getProductName() != ""){

            if(!orderMap.containsKey(order.getId())){
                orderMap.put(order.getId(), order);
            }

            moveOut.setOrder(orderMap.get(order.getId()));
            orderMap.get(order.getId()).getMoveOutList().add(new SoftReference<MoveOut>(moveOut));
            moveOut.setFoundRow(row);
            moveOuts.add(moveOut); 

            this.moveOut = new TikTokMoveOut();
            this.order = new TikTokOrder();
            this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        }
    }

    public List<MoveOut> getMoveOuts() {
        return this.moveOuts;
    }

    public List<Order> getOrders() {
        return new ArrayList<Order>(this.orderMap.values());
    }

    @Override
    protected int getStartDataRow() {
        return 2;
    }

    public TikTokOrderReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            List<MoveOut> moveOuts) {
        super(sharedStrings, stylesTable);
        
        this.moveOuts = moveOuts;
        this.orderMap = new HashMap<String, TikTokOrder>();

        this.order = new TikTokOrder();
        this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        this.moveOut = new TikTokMoveOut();
    }
    
}
