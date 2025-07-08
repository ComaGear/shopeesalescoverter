package com.colbertlum.contentWriter;

import java.util.List;

import com.colbertlum.constants.DateTimePattern;
import com.colbertlum.constants.Columns.RepositoryOrderColumn;
import com.colbertlum.entity.Order;

public class RepositoryOrderMapper implements ContentHeaderMapperInterface<Order> {

    @Override
    public String onCell(String header, Order order) {
        switch (header) {
            case RepositoryOrderColumn.ORDER_ID:
                return order.getId();
            case RepositoryOrderColumn.CREATION_DATE:
                return DateTimePattern.parseString(order.getOrderCreationDate());
            case RepositoryOrderColumn.SHIP_OUT_DATE:
                return
            default:
                return "";
        }
    }

    @Override
    public List<String> onHeader() {
        return List.of(
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
