package com.colbertlum.contentWriter;

import java.util.List;

import com.colbertlum.constants.Columns.RepositoryTikTokOrderColumn;
import com.colbertlum.entity.TikTokOrder;

public class RepositoryTikTokOrderMapper implements ContentHeaderMapperInterface<TikTokOrder> {

    @Override
    public String onCell(String header, TikTokOrder item) {
        switch (header) {
            case RepositoryTikTokOrderColumn.ORDER_ID:
                return item.getId();
            case RepositoryTikTokOrderColumn.STATUS:
                return item.getStatus();
            case RepositoryTikTokOrderColumn.TRACKING_NUMBER:
                return item.getTrackingNumber() != null ? item.getTrackingNumber() : "";
            case RepositoryTikTokOrderColumn.CANCELATION_OR_RETURN_TYPE:
                return item.isReturnRefund() ? "Return/Refund Approved" : "";
            case RepositoryTikTokOrderColumn.TRANSACTION_FEE:
                return Double.toString(item.getTransactionFee());
            case RepositoryTikTokOrderColumn.TIKTOK_SHOP_COMMISION_FEE: 
                return Double.toString(item.getTiktokShopCommisionFee());
            case RepositoryTikTokOrderColumn.SFPSERVICE_FEE:
                return Double.toString(item.getSFPserviceFee());
            case RepositoryTikTokOrderColumn.AFFIlIATE_SHOP_ADS_COMMISION:
                return Double.toString(item.getAffiliateShopAdsCommision());
            case RepositoryTikTokOrderColumn.AFFILIATE_COMMISION:
                return Double.toString(item.getAffiliateCommision());
            case RepositoryTikTokOrderColumn.AFFILIATE_PARTNER_COMMISION:
                return Double.toString(item.getAffiliatePartnerCommision());
            case RepositoryTikTokOrderColumn.TOTAL_SETTLEMENT_AMOUNT:
                return Double.toString(item.getTotalSettlementAmount());
            case RepositoryTikTokOrderColumn.TOTAL_REVENUE:
                return Double.toString(item.getTotalRevenue());
            case RepositoryTikTokOrderColumn.TAP_SHOP_ADS_COMMISION:
                return Double.toString(item.getTAPShopAdsCommision());
            default:
                return "";
        }
    }

    @Override
    public List<String> onHeader() {
        return List.of(
            RepositoryTikTokOrderColumn.ORDER_ID,
            RepositoryTikTokOrderColumn.STATUS,
            RepositoryTikTokOrderColumn.TRACKING_NUMBER,
            RepositoryTikTokOrderColumn.CANCELATION_OR_RETURN_TYPE,
            RepositoryTikTokOrderColumn.TRANSACTION_FEE,
            RepositoryTikTokOrderColumn.TIKTOK_SHOP_COMMISION_FEE, 
            RepositoryTikTokOrderColumn.SFPSERVICE_FEE,
            RepositoryTikTokOrderColumn.AFFIlIATE_SHOP_ADS_COMMISION,
            RepositoryTikTokOrderColumn.AFFILIATE_COMMISION,
            RepositoryTikTokOrderColumn.AFFILIATE_PARTNER_COMMISION,
            RepositoryTikTokOrderColumn.TOTAL_SETTLEMENT_AMOUNT,
            RepositoryTikTokOrderColumn.TOTAL_REVENUE,
            RepositoryTikTokOrderColumn.TAP_SHOP_ADS_COMMISION
        );
    }

}
