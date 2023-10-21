package com.colbertlum.entity;

public class OnlineSalesInfoStatus {

    private OnlineSalesInfo info;
    private String status;

    public OnlineSalesInfo getOnlineSalesInfo() {
        return info;
    }
    public OnlineSalesInfoStatus setOnlineSalesInfo(OnlineSalesInfo info) {
        this.info = info;
        return this;
    }
    public String getStatus() {
        return status;
    }
    public OnlineSalesInfoStatus setStatus(String status) {
        this.status = status;
        return this;
    }

}
