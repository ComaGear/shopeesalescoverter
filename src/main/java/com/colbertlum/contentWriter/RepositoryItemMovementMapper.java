package com.colbertlum.contentWriter;

import java.util.List;

import com.colbertlum.Imputer.Utils.MoveOutFactory;
import com.colbertlum.constants.Columns.RepositoryItemMovementColumn;
import com.colbertlum.entity.MoveOut;

public class RepositoryItemMovementMapper implements ContentHeaderMapperInterface<MoveOut> {

    @Override
    public String onCell(String header, MoveOut item) {
        switch (header) {
            case RepositoryItemMovementColumn.PLATFORM:
                return MoveOutFactory.getPlatform(item);
            case RepositoryItemMovementColumn.ORDER_ID:
                return item.getOrderId();
            case RepositoryItemMovementColumn.SKU:
                return item.getSku();
            case RepositoryItemMovementColumn.NAME:
                return item.getName();
            case RepositoryItemMovementColumn.QUANTITY:
                return Double.toString(item.getQuantity());
            case RepositoryItemMovementColumn.PRICE:
                return Double.toString(item.getPrice());
            case RepositoryItemMovementColumn.PRODUCT_ID:
                return item.getProductId() != null ? item.getProductId() : "";
            case RepositoryItemMovementColumn.AT_TIME_COST:
                return Double.toString(item.getAtTimeCostDouble());
            case RepositoryItemMovementColumn.PRODUCT_NAME:
                return MoveOutFactory.getProductName(item);
            case RepositoryItemMovementColumn.VARIATION_NAME:
                return MoveOutFactory.getVariationName(item);
            case RepositoryItemMovementColumn.PRODUCT_SUBTOTAL:
                return Double.toString(item.getPrice() * item.getQuantity());
            case RepositoryItemMovementColumn.PLATFORM_DISCOUNT_SUBTOTAL:
                return Double.toString(MoveOutFactory.getPlatformDiscount(item));
            default:
                return "";
        }
    }

    @Override
    public List<String> onHeader() {
        return List.of(
            RepositoryItemMovementColumn.PLATFORM,
            RepositoryItemMovementColumn.ORDER_ID,
            RepositoryItemMovementColumn.SKU,
            RepositoryItemMovementColumn.NAME,
            RepositoryItemMovementColumn.QUANTITY,
            RepositoryItemMovementColumn.PRICE,
            RepositoryItemMovementColumn.PRODUCT_ID,
            RepositoryItemMovementColumn.AT_TIME_COST,
            RepositoryItemMovementColumn.PRODUCT_NAME,
            RepositoryItemMovementColumn.VARIATION_NAME,
            RepositoryItemMovementColumn.PRODUCT_SUBTOTAL,
            RepositoryItemMovementColumn.PLATFORM_DISCOUNT_SUBTOTAL
        );
    }

}
