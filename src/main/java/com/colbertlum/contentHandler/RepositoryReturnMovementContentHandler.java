package com.colbertlum.contentHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.constants.PlatformType;
import com.colbertlum.constants.Columns.RepositoryItemMovementColumn;
import com.colbertlum.constants.Columns.RepositoryReturnMovementColumn;
import com.colbertlum.entity.ReturnMoveOut;
import com.colbertlum.entity.ReturnShopeeMoveOut;
import com.colbertlum.entity.ReturnTikTokMoveOut;

public class RepositoryReturnMovementContentHandler extends ContentHandler {

    private List<ReturnMoveOut> returningMoveOuts;
    private ReturnMoveOut returnMoveOut;

    private Map<String, String> valueMap = new HashMap<String, String>();

    @Override
    protected void onCell(String header, int row, String value) { 

        valueMap.put(header, value);

    }


    @Override
    protected void onRow(int row) {
        // if(returnMoveOut.getOrderId() != null) {
        //     returningMoveOuts.add(returnMoveOut);
        // }
        
        // this.returnMoveOut = new ReturnMoveOut();
        
        if(valueMap.get(RepositoryReturnMovementColumn.PLATFORM).equals(PlatformType.SHOPEE)){
            ReturnShopeeMoveOut shopeeMoveOut = new ReturnShopeeMoveOut();
            shopeeMoveOut.setOrderId(valueMap.get(RepositoryReturnMovementColumn.ORDER_ID));
            shopeeMoveOut.setSku(valueMap.get(RepositoryReturnMovementColumn.SKU));
            shopeeMoveOut.setName(valueMap.get(RepositoryReturnMovementColumn.NAME));
            shopeeMoveOut.setQuantity(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.QUANTITY)));
            shopeeMoveOut.setPrice(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.PRICE)));
            shopeeMoveOut.setProductId(valueMap.get(RepositoryReturnMovementColumn.PRODUCT_ID));
            // shopeeMoveOut.setAtTimeCost(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.AT_TIME_COST)));
            // shopeeMoveOut.setProductName(valueMap.get(RepositoryReturnMovementColumn.PRODUCT_NAME));
            // shopeeMoveOut.setVariationName(valueMap.get(RepositoryReturnMovementColumn.VARIATION_NAME));
            shopeeMoveOut.setProductSubtotal(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.PRODUCT_SUBTOTAL)));
            shopeeMoveOut.setPlatformDiscount(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.PLATFORM_DISCOUNT_SUBTOTAL)));
            shopeeMoveOut.setReturnStatus(valueMap.get(RepositoryReturnMovementColumn.RETURN_STATUS));
            shopeeMoveOut.setStatusQuantity(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.STATUS_QUANTITY)));
            shopeeMoveOut.setSettled(valueMap.get(RepositoryReturnMovementColumn.IS_SETTLED).equals(RepositoryReturnMovementColumn.SETTLED_VALUE_TRUE));
            shopeeMoveOut.setReturnedQuantity(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.RETURNED_QUANTITY)));
            
            returnMoveOut = shopeeMoveOut;
        } else if (valueMap.get(RepositoryItemMovementColumn.PLATFORM).equals(PlatformType.TIKTOK)) {
            ReturnTikTokMoveOut tikTokMoveOut = new ReturnTikTokMoveOut();
            tikTokMoveOut.setOrderId(valueMap.get(RepositoryReturnMovementColumn.ORDER_ID));
            tikTokMoveOut.setSku(valueMap.get(RepositoryReturnMovementColumn.SKU));
            tikTokMoveOut.setName(valueMap.get(RepositoryReturnMovementColumn.NAME));
            tikTokMoveOut.setQuantity(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.QUANTITY)));
            tikTokMoveOut.setPrice(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.PRICE)));
            // tikTokMoveOut.setAtTimeCost(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.AT_TIME_COST)));
            // tikTokMoveOut.setProductName(valueMap.get(RepositoryReturnMovementColumn.PRODUCT_NAME));
            // tikTokMoveOut.setVariationName(valueMap.get(RepositoryReturnMovementColumn.VARIATION_NAME));
            tikTokMoveOut.setSKUsubtotalAfterDiscount(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.PRODUCT_SUBTOTAL)));
            tikTokMoveOut.setSKUplatformDiscount(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.PLATFORM_DISCOUNT_SUBTOTAL)));
            tikTokMoveOut.setReturnStatus(valueMap.get(RepositoryReturnMovementColumn.RETURN_STATUS));
            tikTokMoveOut.setStatusQuantity(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.STATUS_QUANTITY)));
            tikTokMoveOut.setSettled(valueMap.get(RepositoryReturnMovementColumn.IS_SETTLED).equals(RepositoryReturnMovementColumn.SETTLED_VALUE_TRUE));
            tikTokMoveOut.setReturnedQuantity(Double.parseDouble(valueMap.get(RepositoryReturnMovementColumn.RETURNED_QUANTITY)));

            returnMoveOut = tikTokMoveOut;
        }

        returningMoveOuts.add(returnMoveOut);
    }

    public RepositoryReturnMovementContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, List<ReturnMoveOut> returningMoveOuts){
        super(sharedStrings, stylesTable);
        this.returningMoveOuts = returningMoveOuts;
        
        returnMoveOut = new ReturnMoveOut();
    }
    
}
