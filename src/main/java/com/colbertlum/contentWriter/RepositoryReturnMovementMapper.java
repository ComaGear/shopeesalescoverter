package com.colbertlum.contentWriter;

import java.util.List;

import com.colbertlum.Imputer.Utils.MoveOutFactory;
import com.colbertlum.constants.Columns.RepositoryReturnMovementColumn;
import com.colbertlum.entity.ReturnMoveOut;

public class RepositoryReturnMovementMapper implements ContentHeaderMapperInterface<ReturnMoveOut> {

    @Override
    public String onCell(String header, ReturnMoveOut item) {
        switch (header) {
            case RepositoryReturnMovementColumn.PLATFORM:
                return MoveOutFactory.getPlatform(item);
            case RepositoryReturnMovementColumn.ORDER_ID:
                return item.getOrderId();
            case RepositoryReturnMovementColumn.SKU:
                return item.getSku();
            case RepositoryReturnMovementColumn.NAME:
                return item.getName();
            case RepositoryReturnMovementColumn.QUANTITY:
                return Double.toString(item.getQuantity());
            case RepositoryReturnMovementColumn.PRICE:
                return Double.toString(item.getPrice());
            case RepositoryReturnMovementColumn.PRODUCT_ID:
                return item.getProductId() != null ? item.getProductId() : "";
            case RepositoryReturnMovementColumn.PRODUCT_SUBTOTAL:
                return Double.toString(item.getPrice() * item.getQuantity());
            case RepositoryReturnMovementColumn.PLATFORM_DISCOUNT_SUBTOTAL:
                return Double.toString(MoveOutFactory.getPlatformDiscount(item));
            case RepositoryReturnMovementColumn.RETURN_STATUS:
                return item.getReturnStatus();
            case RepositoryReturnMovementColumn.STATUS_QUANTITY:
                return Double.toString(item.getStatusQuantity());
            case RepositoryReturnMovementColumn.IS_SETTLED:
                return item.isSettled() ? RepositoryReturnMovementColumn.SETTLED_VALUE_TRUE : "";
            case RepositoryReturnMovementColumn.RETURNED_QUANTITY:
                return Double.toString(item.getReturnedQuantity());
            default:
                return "";
        }
    }

    @Override
    public List<String> onHeader() {
        return List.of(
            RepositoryReturnMovementColumn.PLATFORM,
            RepositoryReturnMovementColumn.ORDER_ID,
            RepositoryReturnMovementColumn.SKU,
            RepositoryReturnMovementColumn.NAME,
            RepositoryReturnMovementColumn.QUANTITY,
            RepositoryReturnMovementColumn.PRICE,
            RepositoryReturnMovementColumn.PRODUCT_ID,
            RepositoryReturnMovementColumn.PRODUCT_SUBTOTAL,
            RepositoryReturnMovementColumn.PLATFORM_DISCOUNT_SUBTOTAL,
            RepositoryReturnMovementColumn.RETURN_STATUS,
            RepositoryReturnMovementColumn.STATUS_QUANTITY,
            RepositoryReturnMovementColumn.IS_SETTLED,
            RepositoryReturnMovementColumn.RETURNED_QUANTITY
        );
    }
    
}
