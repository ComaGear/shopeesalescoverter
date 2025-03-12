package com.colbertlum.Exception;

import java.util.List;

import com.colbertlum.entity.ListingStockReason;

public class ListingStockException extends Exception {
    
    private final List<ListingStockReason> listingStockStatusList;

    public ListingStockException(List<ListingStockReason> onlineSalesInfoStatusList) {
        this.listingStockStatusList = onlineSalesInfoStatusList;
    }

    public List<ListingStockReason> getListingStockStatusList() {
        return listingStockStatusList;
    }

    

}
