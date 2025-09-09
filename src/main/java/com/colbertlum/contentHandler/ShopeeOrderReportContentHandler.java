package com.colbertlum.contentHandler;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.Columns.ShopeeSalesOrderColumn;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ShopeeMoveOut;
import com.colbertlum.entity.ShopeeOrder;

public class ShopeeOrderReportContentHandler extends ContentHandler {

    private List<MoveOut> moveOuts;
    private Map<String, ShopeeOrder> orderMap;
    private ShopeeMoveOut moveOut;
    private ShopeeOrder order;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case ShopeeSalesOrderColumn.ORDER_ID:
                order.setId(value);
                break;
            case ShopeeSalesOrderColumn.ORDER_TOTAL:
                order.setOrderTotalAmount(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.SERVICE_FEE:
                order.setServiceFee(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.COMMISSION_FEE:
                order.setCommissionFee(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.TRANSACTION_FEE:
                order.setTransactionFee(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.SHIPPING_FEE:
                order.setEstimatedShippingFee(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.SKU:
                moveOut.setSku(value);
                break;
            case ShopeeSalesOrderColumn.PARENT_SKU:
                moveOut.setParentSku(value);
                break;
            case ShopeeSalesOrderColumn.VARIATION_NAME:
                moveOut.setVariationName(value);
                break;
            case ShopeeSalesOrderColumn.PRODUCT_NAME:
                moveOut.setProductName(value);
                break;
            case ShopeeSalesOrderColumn.PRICE:
                moveOut.setPrice(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.QUANTITY:
                moveOut.setQuantity(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.SHIP_TIME:
                if(value.isEmpty()) break;
                order.setShipOutDate(DateTimePattern.getLocalDate(value));
                break;
            case ShopeeSalesOrderColumn.ORDER_STATUS:
                order.setStatus(value);
                break;
            case ShopeeSalesOrderColumn.SHIPPING_REBATE_ESTIMATE:
                order.setShippingRebateEstimated(Double.parseDouble(value));
                break;
            case ShopeeSalesOrderColumn.ORDER_CREATION_TIME:
                order.setCreationDate(DateTimePattern.getLocalDate(value));
                break;
            case ShopeeSalesOrderColumn.ORDER_COMPLETED_TIME:
                if(value.isEmpty()) break;
                order.setCompletedDate(DateTimePattern.getLocalDate(value));
                break;
            case ShopeeSalesOrderColumn.TRACKING_NUMBER:
                order.setTrackingNumber(value);
                break;
            case ShopeeSalesOrderColumn.RETURN_REFUND_REQUEST:
                if(!order.isRequestReturnRefundApproved()) {
                    order.setRequestApproved(value.equals(ShopeeSalesOrderColumn.REQUEST_REFUND_APPROVED));
                    moveOut.setReturnRefundRequest(value.equals(ShopeeSalesOrderColumn.REQUEST_REFUND_APPROVED));
                }
                break;
            case ShopeeSalesOrderColumn.RETURNED_QUANTITY:
                moveOut.setReturnedQuantity(Double.parseDouble(value));
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
            if(moveOut.getParentSku() != null && !moveOut.getParentSku().isEmpty()
                    && (moveOut.getSku() == null || moveOut.getSku().isEmpty())) moveOut.setSku(moveOut.getParentSku());
            moveOuts.add(moveOut); 

            this.moveOut = new ShopeeMoveOut();
            this.order = new ShopeeOrder();
            this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        }
    }

    public List<MoveOut> getMoveOuts(){
        return this.moveOuts;
    }

    public List<ShopeeOrder> getShopeeOrders(){
        return new ArrayList<>(orderMap.values());
    }

    public List<Order> getOrders(){
        return new ArrayList<Order>(orderMap.values());
    }


    public ShopeeOrderReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            List<MoveOut> moveOuts) {
        super(sharedStrings, stylesTable);

        this.moveOuts = moveOuts;
        this.orderMap = new HashMap<String, ShopeeOrder>();

        this.order = new ShopeeOrder();
        this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        this.moveOut = new ShopeeMoveOut();
    }
}
