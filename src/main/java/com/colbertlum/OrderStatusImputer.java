package com.colbertlum;

import java.util.Map;

public class OrderStatusImputer {

    enum status {
        COMPLETED,
        SHIPPING, // order has ship out time
        PENDING,
        CANCELLED,
        RETURNING
    }

    private Map<String, status> orders;
    
    public void loadFile(){

    }

    public void saveFile(){
        
    }
}
