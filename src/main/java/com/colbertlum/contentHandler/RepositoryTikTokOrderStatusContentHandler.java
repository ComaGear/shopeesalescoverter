package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.Columns.RepositoryTikTokOrderColumn;
import com.colbertlum.entity.TikTokOrder;

public class RepositoryTikTokOrderStatusContentHandler extends ContentHandler{

    private TikTokOrder order;
    private List<TikTokOrder> orders;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case RepositoryTikTokOrderColumn.ORDER_ID:
                order.setId(value);
                break;
            case RepositoryTikTokOrderColumn.STATUS:
                order.setStatus(value);
                break;
            case RepositoryTikTokOrderColumn.TRACKING_NUMBER:
                order.setTrackingNumber(value);
                break;
            case RepositoryTikTokOrderColumn.CANCELATION_OR_RETURN_TYPE:
                if(!value.isEmpty()) order.setReturnRefund(true);
                break;
            case RepositoryTikTokOrderColumn.TRANSACTION_FEE:
                order.setTransactionFee(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.TIKTOK_SHOP_COMMISION_FEE:
                order.setTiktokShopCommisionFee(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.SFPSERVICE_FEE:
                order.setSFPserviceFee(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.AFFIlIATE_SHOP_ADS_COMMISION:
                order.setAffiliateShopAdsCommision(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.AFFILIATE_COMMISION:
                order.setAffiliateCommision(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.AFFILIATE_PARTNER_COMMISION:
                order.setAffiliatePartnerCommision(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.TOTAL_SETTLEMENT_AMOUNT:
                order.setTotalSettlementAmount(Double.parseDouble(value));
                break;
            case RepositoryTikTokOrderColumn.TOTAL_REVENUE:
                order.setTotalRevenue(Double.parseDouble(value));
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(order != null && order.getId() != null) orders.add(order);
    }
    
    public RepositoryTikTokOrderStatusContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,  List<TikTokOrder> orders) {
        super(sharedStrings, stylesTable);
        this.order = new TikTokOrder();
        this.orders = orders;
    }
}
