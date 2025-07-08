package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.constants.Columns.IncomeTikTokSettlementColumn;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.TikTokOrder;

public class TikTokIncomeReportContentHandler extends ContentHandler {

    private List<TikTokOrder> orders;
    private TikTokOrder order;

    @Override
    protected void onCell(String header, int row, String value) {
        super.onCell(header, row, value);
        switch (header) {
            case IncomeTikTokSettlementColumn.TRANSACTION_FEE:
                order.setTransactionFee(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.TIKTOK_SHOP_COMMISION_FEE:
                order.setTiktokShopCommisionFee(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.SFP_SERVICE_FEE:
                order.setSFPserviceFee(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.PLATFORM_SHIPPING_FEE_DISCOUNT:
                order.setPlatformShippingFeeDiscount(Double.parseDouble(value));
                break;
            case IncomeTikTokSettlementColumn.CUSTOMER_PAID_SHIPPING_FEE:
                order.setCustomerPaidShippingFee(Double.parseDouble(value));
                break;
            case IncomeTikTokSettlementColumn.ACTUAL_SHIPPING_FEE:
                order.setActualShippingFee(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.SELLER_SHIPPING_FEE:
                order.setSellerShippingFee(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.AFFILIATE_SHOP_ADS_COMMISION:
                order.setAffiliateShopAdsCommision(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.AFFILIATE_COMMISION:
                order.setAffiliateCommision(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.AFFILIATE_PARTNER_COMMISION:
                order.setAffiliateShopAdsCommision(Double.parseDouble(value) * -1);
                break;
            case IncomeTikTokSettlementColumn.TOTAL_SETTLEMENT_AMOUNT:
                order.setTotalSettlementAmount(Double.parseDouble(value));
                break;
            case IncomeTikTokSettlementColumn.TOTAL_REVENUE:
                order.setTotalRevenue(Double.parseDouble(value));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        Order lookupOrder = Lookup.lookupOrder(orders, order.getId());
        if(lookupOrder instanceof TikTokOrder){
            TikTokOrder foundTikTokOrder = (TikTokOrder) lookupOrder;
            foundTikTokOrder.setTransactionFee(order.getTransactionFee());
            foundTikTokOrder.setTiktokShopCommisionFee(order.getTiktokShopCommisionFee());
            foundTikTokOrder.setSFPserviceFee(order.getSFPserviceFee());
            foundTikTokOrder.setPlatformShippingFeeDiscount(order.getPlatformShippingFeeDiscount());
            foundTikTokOrder.setCustomerPaidShippingFee(order.getCustomerPaidShippingFee());
            foundTikTokOrder.setActualShippingFee(order.getActualShippingFee());
            foundTikTokOrder.setSellerDiscount(order.getSellerDiscount());
            foundTikTokOrder.setAffiliateShopAdsCommision(order.getAffiliateShopAdsCommision());
            foundTikTokOrder.setAffiliateCommision(order.getAffiliateCommision());
            foundTikTokOrder.setAffiliatePartnerCommision(order.getAffiliatePartnerCommision());
            foundTikTokOrder.setTotalSettlementAmount(order.getTotalSettlementAmount());
            foundTikTokOrder.setTotalRevenue(order.getTotalRevenue());
        }

        this.order = new TikTokOrder();
    }
    
    public TikTokIncomeReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            List<TikTokOrder> orders) {
        super(sharedStrings, stylesTable);
        this.order = new TikTokOrder();
        this.orders = orders;
    }
}
