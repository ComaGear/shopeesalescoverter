package com.colbertlum.contentWriter;

import java.util.List;

import com.colbertlum.Imputer.Utils.OrderFactory;
import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.Columns.RepositoryOrderColumn;
import com.colbertlum.entity.Order;

public class RepositoryOrderMapper implements ContentHeaderMapperInterface<Order> {

    @Override
    public String onCell(String header, Order order) {
        switch (header) {
            case RepositoryOrderColumn.PLATFORM:
                return OrderFactory.getPlatform(order);
            case RepositoryOrderColumn.ORDER_ID:
                return order.getId();
            case RepositoryOrderColumn.CREATION_DATE:
                return DateTimePattern.parseString(order.getCreationDate());
            case RepositoryOrderColumn.SHIP_OUT_DATE:
                return DateTimePattern.parseString(order.getShipOutDate());
            case RepositoryOrderColumn.COMPLETED_DATE:
                return order.getCompletedDate() != null ? DateTimePattern.parseString(order.getCompletedDate()) : "";
            case RepositoryOrderColumn.SETTLED_DATE:
                return order.getSettledDate() != null ? DateTimePattern.parseString(order.getSettledDate()) : "";
            case RepositoryOrderColumn.MANAGEMENT_FEE:
                return Double.toString(order.getManagementFee());
            case RepositoryOrderColumn.ADJUSTMENT_SHIPPING_FEE:
                return Double.toString(order.getAdjustmentShippingFee());
            case RepositoryOrderColumn.SELLER_REBATE:
                return Double.toString(order.getSellerRebate());
            case RepositoryOrderColumn.PLATFORM_REBATE:
                return Double.toString(order.getPlatformRebate());
            case RepositoryOrderColumn.ORDER_TOTAL_AMOUNT:
                return Double.toString(order.getOrderTotalAmount());
            case RepositoryOrderColumn.INTERNAL_STATUS:
                return order.getInternalStatus();
            default:
                return "";
        }
    }

    @Override
    public List<String> onHeader() {
        return List.of(
            RepositoryOrderColumn.PLATFORM,
            RepositoryOrderColumn.ORDER_ID,
            RepositoryOrderColumn.CREATION_DATE,
            RepositoryOrderColumn.SHIP_OUT_DATE,
            RepositoryOrderColumn.COMPLETED_DATE,
            RepositoryOrderColumn.SETTLED_DATE,
            RepositoryOrderColumn.MANAGEMENT_FEE,
            RepositoryOrderColumn.ADJUSTMENT_SHIPPING_FEE,
            RepositoryOrderColumn.SELLER_REBATE,
            RepositoryOrderColumn.PLATFORM_REBATE,
            RepositoryOrderColumn.ORDER_TOTAL_AMOUNT,
            RepositoryOrderColumn.INTERNAL_STATUS);
    }
    
}
