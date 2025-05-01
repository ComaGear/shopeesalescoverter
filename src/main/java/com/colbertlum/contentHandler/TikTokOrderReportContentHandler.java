package com.colbertlum.contentHandler;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.StylesTable;

import com.colbertlum.entity.MoveOut;
import com.colbertlum.entity.Order;
import com.colbertlum.entity.ShopeeMoveOut;
import com.colbertlum.entity.ShopeeOrder;
import com.colbertlum.entity.TikTokMoveOut;
import com.colbertlum.entity.TikTokOrder;

public class TikTokOrderReportContentHandler extends ContentHandler {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private List<MoveOut> moveOuts;
    private Map<String, TikTokOrder> orderMap;
    private TikTokMoveOut moveOut;
    private TikTokOrder order;

    
    @Override
    protected void onCell(String header, int row, String value) {
        // TODO Auto-generated method stub
        super.onCell(header, row, value);
    }

    @Override
    protected void onRow(int row) {
        if(moveOut.getProductName() != null && moveOut.getProductName() != ""){

            if(!orderMap.containsKey(order.getId())){
                orderMap.put(order.getId(), order);
            }

            moveOut.setOrder(orderMap.get(order.getId()));
            orderMap.get(order.getId()).getMoveOutList().add(new SoftReference<MoveOut>(moveOut));
            moveOut.setFoundRow(row);
            moveOuts.add(moveOut); 

            this.moveOut = new TikTokMoveOut();
            this.order = new TikTokOrder();
            this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        }
    }

    public List<MoveOut> getMoveOuts() {
        return this.moveOuts;
    }

    public List<Order> getOrders() {
        return new ArrayList<Order>(this.orderMap.values());
    }

    public TikTokOrderReportContentHandler(SharedStrings sharedStrings, StylesTable stylesTable,
            List<MoveOut> moveOuts) {
        super(sharedStrings, stylesTable);
        
        this.moveOuts = moveOuts;
        this.orderMap = new HashMap<String, TikTokOrder>();

        this.order = new TikTokOrder();
        this.order.setMoveOutList(new ArrayList<SoftReference<MoveOut>>());
        this.moveOut = new TikTokMoveOut();
    }
    
}
