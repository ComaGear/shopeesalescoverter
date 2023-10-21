package com.colbertlum.Exception;

import java.util.List;

import com.colbertlum.entity.OnlineSalesInfoStatus;

public class OnlineSalesInfoException extends Exception {
    
    private final List<OnlineSalesInfoStatus> onlineSalesInfoStatusList;

    public OnlineSalesInfoException(List<OnlineSalesInfoStatus> onlineSalesInfoStatusList) {
        this.onlineSalesInfoStatusList = onlineSalesInfoStatusList;
    }

    public List<OnlineSalesInfoStatus> getOnlineSalesInfoStatusList() {
        return onlineSalesInfoStatusList;
    }

    

}
