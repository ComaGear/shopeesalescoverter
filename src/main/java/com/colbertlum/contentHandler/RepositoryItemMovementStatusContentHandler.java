package com.colbertlum.contentHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.PlatformType;
import com.colbertlum.constants.Columns.RepositoryItemMovementColumn;
import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.ShopeeMoveOut;
import com.colbertlum.entity.TikTokMoveOut;

public class RepositoryItemMovementStatusContentHandler extends ContentHandler{


    
    private MoveOut moveOut;
    private List<MoveOut> moveOuts;

    private Map<String, String> valueMap = new HashMap<String, String>();

    @Override
    protected void onCell(String header, int row, String value) {

        valueMap.put(header, value);

        
    }

    @Override
    protected void onRow(int row) {
        // if(itemMap.containsKey(itemMovementStatus.getOrderId())){
        //     itemMap.get(itemMovementStatus.getOrderId()).add(itemMovementStatus);
        // } else {
        //     ArrayList<ItemMovementStatus> itemList = new ArrayList<ItemMovementStatus>();
        //     itemList.add(itemMovementStatus);
        //     itemMap.put(itemMovementStatus.getOrderId(), itemList);
        // }
        // itemMovementStatus = new ItemMovementStatus();

        if(valueMap.get(RepositoryItemMovementColumn.PLATFORM).equals(PlatformType.SHOPEE)){
            ShopeeMoveOut shopeeMoveOut = new ShopeeMoveOut();
            shopeeMoveOut.setOrderId(valueMap.get(RepositoryItemMovementColumn.ORDER_ID));
            shopeeMoveOut.setSku(valueMap.get(RepositoryItemMovementColumn.SKU));
            shopeeMoveOut.setName(valueMap.get(RepositoryItemMovementColumn.NAME));
            shopeeMoveOut.setQuantity(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.QUANTITY)));
            shopeeMoveOut.setPrice(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.PRICE)));
            shopeeMoveOut.setProductId(valueMap.get(RepositoryItemMovementColumn.PRODUCT_ID));
            shopeeMoveOut.setAtTimeCost(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.AT_TIME_COST)));
            shopeeMoveOut.setProductName(valueMap.get(RepositoryItemMovementColumn.PRODUCT_NAME));
            shopeeMoveOut.setVariationName(valueMap.get(RepositoryItemMovementColumn.VARIATION_NAME));
            shopeeMoveOut.setProductSubtotal(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.PRODUCT_SUBTOTAL)));
            shopeeMoveOut.setPlatformDiscount(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.PLATFORM_DISCOUNT)));

            moveOut = shopeeMoveOut;
        } else if (valueMap.get(RepositoryItemMovementColumn.PLATFORM).equals(PlatformType.TIKTOK)) {
            TikTokMoveOut tikTokMoveOut = new TikTokMoveOut();
            tikTokMoveOut.setOrderId(valueMap.get(RepositoryItemMovementColumn.ORDER_ID));
            tikTokMoveOut.setSku(valueMap.get(RepositoryItemMovementColumn.SKU));
            tikTokMoveOut.setName(valueMap.get(RepositoryItemMovementColumn.NAME));
            tikTokMoveOut.setQuantity(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.QUANTITY)));
            tikTokMoveOut.setPrice(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.PRICE)));
            tikTokMoveOut.setAtTimeCost(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.AT_TIME_COST)));
            tikTokMoveOut.setProductName(valueMap.get(RepositoryItemMovementColumn.PRODUCT_NAME));
            tikTokMoveOut.setVariationName(valueMap.get(RepositoryItemMovementColumn.VARIATION_NAME));
            tikTokMoveOut.setSKUsubtotalAfterDiscount(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.PRODUCT_SUBTOTAL)));
            tikTokMoveOut.setSKUplatformDiscount(Double.parseDouble(valueMap.get(RepositoryItemMovementColumn.PLATFORM_DISCOUNT)));

            moveOut = tikTokMoveOut;
        }

        moveOuts.add(moveOut);
        // moveOut = new MoveOut();
    }

    public RepositoryItemMovementStatusContentHandler(SharedStrings sharedStringsTable, StylesTable stylesTable,
            List<MoveOut> moveOuts) {
        super(sharedStringsTable, stylesTable);
        this.moveOuts = moveOuts;
    }
    
    
}
