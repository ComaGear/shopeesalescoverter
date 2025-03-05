package com.colbertlum.Exception;

import java.util.List;

import com.colbertlum.entity.ListingStockReason;

public class OnlineSalesInfoException extends Exception {
    
    private final List<ListingStockReason> onlineSalesInfoStatusList;

    public OnlineSalesInfoException(List<ListingStockReason> onlineSalesInfoStatusList) {
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
    }

    public List<ListingStockReason> getOnlineSalesInfoStatusList() {
        return onlineSalesInfoStatusList;
    }

    

}
