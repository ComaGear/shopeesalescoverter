package com.colbertlum.contentWriter;

import java.util.List;

import com.colbertlum.constants.Columns.RepositoryShopeeOrderColumn;
import com.colbertlum.entity.ShopeeOrder;

public class RepositoryShopeeOrderMapper implements ContentHeaderMapperInterface<ShopeeOrder> {

    @Override
    public String onCell(String header, ShopeeOrder item) {
        switch (header) {
            case RepositoryShopeeOrderColumn.ORDER_ID:
                return item.getId();
            case RepositoryShopeeOrderColumn.REQUEST_RETURN_REFUND:
                return item.isRequestApproved() ? "Request Approved" : "";
            case RepositoryShopeeOrderColumn.STATUS:
                return item.getStatus();
            case RepositoryShopeeOrderColumn.TRANSACTION_FEE:
                return Double.toString(item.getTransactionFee());
            case RepositoryShopeeOrderColumn.SERVICE_FEE:
                return Double.toString(item.getServiceFee());
            case RepositoryShopeeOrderColumn.COMMISION_FEE:
                return Double.toString(item.getCommissionFee());
            case RepositoryShopeeOrderColumn.SHOPEE_VOUCHER:
                return Double.toString(item.getShopeeVoucher());
            case RepositoryShopeeOrderColumn.SELLER_ABSORBED_COIN_CASHBACK:
                return Double.toString(item.getSellerAbsorbedCoinCashback());
            case RepositoryShopeeOrderColumn.SELLER_VOUCHER:
                return Double.toString(item.getSellerVoucher());
            case RepositoryShopeeOrderColumn.ESTIMATED_SHIPPING_FEE:
                return Double.toString(item.getEstimatedShippingFee()); 
            case RepositoryShopeeOrderColumn.BUYER_PAID_SHIPPING_FEE:
                return Double.toString(item.getBuyerPaidShippingFee());
            case RepositoryShopeeOrderColumn.SHIPPING_REBATE_ESTIMATED:
                return Double.toString(item.getShippingRebateEstimated());
            case RepositoryShopeeOrderColumn.TRACKING_NUMBER:
                return item.getTrackingNumber() != null ? item.getTrackingNumber() : "";
            default:
                return "";
        }
    }

    @Override
    public List<String> onHeader() {
        return List.of(
            RepositoryShopeeOrderColumn.ORDER_ID,
            RepositoryShopeeOrderColumn.REQUEST_RETURN_REFUND,
            RepositoryShopeeOrderColumn.STATUS,
            RepositoryShopeeOrderColumn.TRANSACTION_FEE,
            RepositoryShopeeOrderColumn.SERVICE_FEE,
            RepositoryShopeeOrderColumn.COMMISION_FEE,
            RepositoryShopeeOrderColumn.SHOPEE_VOUCHER,
            RepositoryShopeeOrderColumn.SELLER_ABSORBED_COIN_CASHBACK,
            RepositoryShopeeOrderColumn.SELLER_VOUCHER,
            RepositoryShopeeOrderColumn.ESTIMATED_SHIPPING_FEE,
            RepositoryShopeeOrderColumn.BUYER_PAID_SHIPPING_FEE,
            RepositoryShopeeOrderColumn.SHIPPING_REBATE_ESTIMATED,
            RepositoryShopeeOrderColumn.TRACKING_NUMBER
        );
    }


}

