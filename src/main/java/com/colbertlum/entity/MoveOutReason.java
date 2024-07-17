package com.colbertlum.entity;

public class MoveOutReason {

    public static final String EMPTY = "Empty sku";
    public static final String NOT_EXIST_SKU = "SKU not found at meas";
    
    private MoveOut moveOut;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MoveOut getMoveOut() {
        return moveOut;
    }

    public void setMoveOut(MoveOut moveOut) {
        this.moveOut = moveOut;
    }

    public MoveOutReason(String status, MoveOut moveOut){
        this.status = status;
        this.moveOut = moveOut;
    }
}
