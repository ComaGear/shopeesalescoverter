package com.colbertlum.entity;

public class OnlineSalesInfoReason {

    private OnlineSalesInfo info;
    private String status;

    public OnlineSalesInfo getOnlineSalesInfo() {
        return info;
    }
    public OnlineSalesInfoReason setOnlineSalesInfo(OnlineSalesInfo info) {
        this.info = info;
        return this;
    }
    public String getStatus() {
        return status;
    }
    public OnlineSalesInfoReason setStatus(String status) {
        this.status = status;
        return this;
    }

}
