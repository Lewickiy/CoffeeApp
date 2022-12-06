package com.lewickiy.coffeeboardapp.entities.info;

public class Info {
    public static Info info;
    long infoId;
    int outletId;
    String message;
    Boolean delivered;

    public Info() {
    }
    public long getInfoId() {
        return infoId;
    }
    public void setInfoId(long infoId) {
        this.infoId = infoId;
    }
    public int getOutletId() {
        return outletId;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setDelivered(Boolean delivered) {
        this.delivered = delivered;
    }
}
