package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.Imputer.Utils.MoveOutFactory;
import com.colbertlum.entity.ReturnMoveOut;

public class RepositoryReturnMovementContentHandler extends ContentHandler {

    private static final String ORDER_ID = "Order Id";
    private static final String SKU = "SKU";
    private static final String PRODUCT_NAME = "Product Name";
    private static final String VARIATION_NAME = "Variation Name";
    private static final String QUANTITY = "Quantity";
    private static final String PRICE = "Price";
    private static final String RETURN_STATUS = "Return Status";
    private static final String STATUS_QUANTITY = "Status Quantity";

    private List<ReturnMoveOut> returningMoveOuts;
    private ReturnMoveOut returnMoveOut;

    @Override
    protected void onCell(String header, int row, String value) { 
        switch (header) {
            case ORDER_ID:
                returnMoveOut.setOrderId(value);
                break;
            case SKU:
                returnMoveOut.setSku(value);
                break;
            case PRODUCT_NAME:
                MoveOutFactory.setProductName(returnMoveOut, value);
                break;
            case VARIATION_NAME:
                MoveOutFactory.setVariationName(returnMoveOut, value);
                break;
            case QUANTITY:
                returnMoveOut.setQuantity(Double.parseDouble(value));
                break;
            case PRICE:
                returnMoveOut.setPrice(Double.parseDouble(value));
                break;
            case RETURN_STATUS:
                returnMoveOut.setReturnStatus(value);
                break;
            case STATUS_QUANTITY:
                returnMoveOut.setStatusQuantity(Double.parseDouble(value));
                break;
        
            default:
                break;
        }    
    }


    @Override
    protected void onRow(int row) {
        if(returnMoveOut.getOrderId() != null) {
            returningMoveOuts.add(returnMoveOut);
        }
        
        this.returnMoveOut = new ReturnMoveOut();
        
    }

    public RepositoryReturnMovementContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, List<ReturnMoveOut> returningMoveOuts){
        super(sharedStrings, stylesTable);
        this.returningMoveOuts = returningMoveOuts;
        
        returnMoveOut = new ReturnMoveOut();
    }
    
}
