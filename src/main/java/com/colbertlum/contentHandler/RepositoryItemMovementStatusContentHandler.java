package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.MoveOut;

public class RepositoryItemMovementStatusContentHandler extends ContentHandler{

    private static final String ORDER_ID = "Order ID";
    private static final String SKU = "SKU";
    private static final String QUANTITY = "Quantity";
    private static final String PRODUCT_NAME = "Product Name";
    private static final String VARIATION_NAME = "Variation Name";
    private static final String PRICE = "Price";

    private MoveOut moveOut;
    private List<MoveOut> moveOuts;

    @Override
    protected void onCell(String header, int row, String value) {
        switch (header) {
            case ORDER_ID:
                moveOut.setOrderId(value);
                break;
            case SKU:
                moveOut.setSku(value);
                break;
            case QUANTITY:
                moveOut.setQuantity(Double.parseDouble(value));
                break;
            case PRODUCT_NAME:
                moveOut.setProductName(value);
                break;
            case VARIATION_NAME:
                moveOut.setVariationName(value);
                break;
            case PRICE:
                moveOut.setPrice(Double.parseDouble(value));
            default:
                break;
        }
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

        moveOuts.add(moveOut);

        moveOut = new MoveOut();
    }

    public RepositoryItemMovementStatusContentHandler(SharedStrings sharedStringsTable, StylesTable stylesTable,
            List<MoveOut> moveOuts) {
        super(sharedStringsTable, stylesTable);
        this.moveOuts = moveOuts;

        moveOut = new MoveOut();
    }
    
    
}
