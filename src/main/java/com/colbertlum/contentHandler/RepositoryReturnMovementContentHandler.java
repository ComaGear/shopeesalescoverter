package com.colbertlum.contentHandler;

import java.util.List;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.ReturnMoveOut;

public class RepositoryReturnMovementContentHandler extends ContentHandler {

    private List<ReturnMoveOut> returningMoveOuts;

    @Override
    protected void onCell(String header, int row, String value) {
        // TODO Auto-generated method stub
        super.onCell(header, row, value);
    }

    @Override
    protected void onRow(int row) {
        // TODO Auto-generated method stub
        super.onRow(row);
    }

    public RepositoryReturnMovementContentHandler(SharedStrings sharedStrings, StylesTable stylesTable, List<ReturnMoveOut> returningMoveOuts){
        super(sharedStrings, stylesTable);
        this.returningMoveOuts = returningMoveOuts;
    }
    
}
