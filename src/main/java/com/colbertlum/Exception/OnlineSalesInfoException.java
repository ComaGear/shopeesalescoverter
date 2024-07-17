package com.colbertlum.Exception;

import java.util.List;

import com.colbertlum.entity.OnlineSalesInfoReason;

public class OnlineSalesInfoException extends Exception {
    
    private final List<OnlineSalesInfoReason> onlineSalesInfoStatusList;

    public OnlineSalesInfoException(List<OnlineSalesInfoReason> onlineSalesInfoStatusList) {
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
    }

    public List<OnlineSalesInfoReason> getOnlineSalesInfoStatusList() {
        return onlineSalesInfoStatusList;
    }

    

}
