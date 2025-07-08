package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.Columns.RepositoryShopeeOrderColumn;
import com.colbertlum.entity.ShopeeOrder;

public class RepositoryShopeeOrderStatusContentHandler extends ContentHandler {

    // private static final String SHIPPING_FEE = "Shipping Fee";
    // private static final String SHIPPING_REBATE = "Shipping Rebate";
    
    private List<ShopeeOrder> orders;
    private ShopeeOrder order;

    @Override
    protected void onRow(int row) {

        if(order.getId() != null){
            orders.add(order);
        }
        order = new ShopeeOrder();
    }

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case RepositoryShopeeOrderColumn.ORDER_ID:
                order.setId(value);
                break;
            case RepositoryShopeeOrderColumn.TRACKING_NUMBER:
                order.setTrackingNumber(value);
                break;
            case RepositoryShopeeOrderColumn.REQUEST_RETURN_REFUND:
                order.setRequestApproved(value.equals("Request Approved"));
                break;
            case RepositoryShopeeOrderColumn.STATUS:
                order.setStatus(value);
                break;
            case RepositoryShopeeOrderColumn.TRANSACTION_FEE:
                order.setTransactionFee(Double.parseDouble(value));
                break;
            case RepositoryShopeeOrderColumn.SERVICE_FEE:
                order.setServiceFee(Double.parseDouble(value));
                break;
            case RepositoryShopeeOrderColumn.COMMISION_FEE:
                order.setCommissionFee(Double.parseDouble(value));
                break;
            case RepositoryShopeeOrderColumn.SHOPEE_VOUCHER:
                order.setShopeeVoucher(Double.parseDouble(value));
                break;
            case RepositoryShopeeOrderColumn.SELLER_ABSORBED_COIN_CASHBACK:
                order.setSellerAbsorbedCoinCashback(Double.parseDouble(value));
                break;
            case RepositoryShopeeOrderColumn.SELLER_VOUCHER:
                order.setSellerVoucher(Double.parseDouble(value));
                break;
            default:
                break;
        }
    }

    public RepositoryShopeeOrderStatusContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,  List<ShopeeOrder> orders) {
        super(sharedStrings, stylesTable);
        this.order = new ShopeeOrder();
        this.orders = orders;
    }
    
}
