package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.Imputer.Utils.Lookup;
import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.Columns.IncomeShopeeSettlementColumn;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ShopeeOrder;

public class ShopeeIncomeReportContentHandler extends ContentHandler {

    private List<ShopeeOrder> orders;
    private ShopeeOrder order;
    private String viewBy;

    @Override
    protected int getHeaderRow() {
        return 2;
    }

    @Override
    protected int getStartDataRow() {
        return 3;
    }

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case IncomeShopeeSettlementColumn.ORDER_ID:
                order.setSettledDate(DateTimePattern.getLocalDate(value));
                break;
            case IncomeShopeeSettlementColumn.VIEW_BY:
                viewBy = value;
                break;
            case IncomeShopeeSettlementColumn.TOTAL_RELEASED_AMOUNT:
                order.setPlatformReleaseAmount(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.RETURN_TO_SELLER_FEE:
                order.setReturnToSellerFee(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.SHIPPING_FEE_CHARGED_BY_LOGISTIC:
                order.setActualShippingFee(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.SELLER_PAID_SHIPPING_FEE_SST:
                order.setSellerPaidShippingFeeSST(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.REVERSE_SHIPPING_FEE:
                order.setReverseShippingFee(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.REVERSE_SHIPPING_FEE_SST:
                order.setReverseShippingFeeSST(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.REBATE_PROVIDED_BY_SHOPEE:
                order.setShopeeRebate(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.AMS_COMMISION_FEE:
                order.setAmsCommisionFee(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.SAVER_PROGRAMME_FEE:
                order.setSaverProgrammeFee(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.BUYER_PAID_INSTALLATION_FEE:
                order.setBuyerPaidInstallationFee(Double.parseDouble(value));
                break;
            case IncomeShopeeSettlementColumn.ACTUAL_INSTALLATION_FEE:
                order.setActualInstallationFee(Double.parseDouble(value));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRow(int row) {
        if(viewBy != null && !viewBy.isEmpty() && viewBy.equals(IncomeShopeeSettlementColumn.VIEW_BY_VALUE_ORDER)){
            Order lOrder = Lookup.lookupOrder(orders, order.getId());
            if(lOrder instanceof ShopeeOrder) {
                ShopeeOrder lookupOrder = (ShopeeOrder) lOrder;
                lookupOrder.setSettledDate(order.getSettledDate());
                lookupOrder.setPlatformReleaseAmount(order.getPlatformReleaseAmount());
                lookupOrder.setReturnToSellerFee(order.getReturnToSellerFee());
                lookupOrder.setActualShippingFee(order.getActualShippingFee());
                lookupOrder.setSellerPaidShippingFeeSST(order.getSellerPaidShippingFeeSST());
                lookupOrder.setReverseShippingFee(order.getReverseShippingFee());
                lookupOrder.setReverseShippingFeeSST(order.getReverseShippingFeeSST());
                lookupOrder.setShopeeRebate(order.getShopeeRebate());
                lookupOrder.setAmsCommisionFee(order.getAmsCommisionFee());
                lookupOrder.setSaverProgrammeFee(order.getSaverProgrammeFee());
                lookupOrder.setBuyerPaidInstallationFee(order.getBuyerPaidInstallationFee());
                lookupOrder.setActualInstallationFee(order.getActualInstallationFee());
            }
        }
    }
    
    public ShopeeIncomeReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            List<ShopeeOrder> orders){
        super(sharedStrings, stylesTable);
        this.order = new ShopeeOrder();
        this.orders = orders;
    }
}
