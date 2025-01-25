package com.colbertlum.contentHandler;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;

public class ShopeeOrderReportContentHandler extends ContentHandler {


    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private static final String ORDER_ID = "Order ID";
    private static final String ORDER_TOTAL = "Total Amount";
    private static final String SERVICE_FEE = "Service Fee";
    private static final String COMMISSION_FEE = "Commission Fee";
    private static final String TRANSACTION_FEE = "Transaction Fee";
    private static final String SHIPPING_FEE = "Estimated Shipping Fee";
    private static final String SHIPPING_REBATE_ESTIMATE = "Shipping Rebate Estimate";

    private static final String SKU = "SKU Reference No.";
    private static final String PARENT_SKU = "Parent SKU Reference No.";
    private static final String VARIATION_NAME = "Variation Name";
    private static final String PRODUCT_NAME = "Product Name";
    private static final String PRICE = "Deal Price";
    private static final String QUANTITY = "Quantity";

    private static final String ORDER_STATUS = "Order Status";
    private static final String SHIP_TIME = "Ship Time";
    private static final String ORDER_CREATION_TIME = "Order Creation Date";
    private static final String ORDER_COMPLETED_TIME = "Order Complete Time";
    private static final String TRACKING_NUMBER = "Tracking Number*";
    private static final String RETURN_REFUND_REQUEST = "Return / Refund Status";

    private ArrayList<MoveOut> moveOuts;
    private Map<String, Order> orderMap;
    private MoveOut moveOut;
    private Order order;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case ORDER_ID:
                order.setId(value);
                break;
            case ORDER_TOTAL:
                order.setOrderTotalAmount(Double.parseDouble(value));
                break;
            case SERVICE_FEE:
                order.setServiceFee(Double.parseDouble(value));
                break;
            case COMMISSION_FEE:
                order.setCommissionFee(Double.parseDouble(value));
                break;
            case TRANSACTION_FEE:
                order.setTransactionFee(Double.parseDouble(value));
                break;
            case SHIPPING_FEE:
                order.setShippingFee(Double.parseDouble(value));
                break;
            case SKU:
                moveOut.setSku(value);
                break;
            case PARENT_SKU:
                moveOut.setParentSku(value);
                break;
            case VARIATION_NAME:
                moveOut.setVariationName(value);
                break;
            case PRODUCT_NAME:
                moveOut.setProductName(value);
                break;
            case PRICE:
                moveOut.setPrice(Double.parseDouble(value));
                break;
            case QUANTITY:
                moveOut.setQuantity(Double.parseDouble(value));
                break;
            case SHIP_TIME:
                if(value.isEmpty()) break;
                order.setShipOutDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case ORDER_STATUS:
                order.setStatus(value);
                break;
            case SHIPPING_REBATE_ESTIMATE:
                order.setShippingRebateEstimate(Double.parseDouble(value));
                break;
            case ORDER_CREATION_TIME:
                order.setOrderCreationDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case ORDER_COMPLETED_TIME:
                order.setOrderCompleteDate(LocalDateTime.parse(value, DateTimeFormatter.ofPattern(DATE_PATTERN)).toLocalDate());
                break;
            case TRACKING_NUMBER:
                order.setTrackingNumber(value);
                break;
            case RETURN_REFUND_REQUEST:
                order.setRequestApproved(value.equals(value));
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

            this.moveOut = new MoveOut();
            this.order = new Order();
            this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        }
    }

    public List<MoveOut> getMoveOuts(){
        return this.moveOuts;
    }

    public List<Order> getOrders(){
        return new ArrayList<>(orderMap.values());
    }


    public ShopeeOrderReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            ArrayList<MoveOut> moveOuts) {
        super(sharedStrings, stylesTable);

        this.moveOuts = moveOuts;
        this.orderMap = new HashMap<String, Order>();
    }
}
