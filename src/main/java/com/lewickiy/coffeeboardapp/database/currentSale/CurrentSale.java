package com.lewickiy.coffeeboardapp.database.currentSale;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

import static com.lewickiy.coffeeboardapp.database.Query.insertToSql;

public class CurrentSale {
    private int saleId;
    private int userId;
    private int outletId;
    private Date currentDate;
    private Time currentTime;
    private int paymentTypeId;
    private int clientId;

    public CurrentSale(int saleId, int userId, int outletId) {
        this.saleId = saleId;
        this.userId = userId;
        this.outletId = outletId;
    }

    public int getSaleId() {
        return saleId;
    }
    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSaleOutletId() {
        return outletId;
    }

    public void setSaleOutletId(int outletId) {
        this.outletId = outletId;
    }

    public Date getCurrentDate() {
        return currentDate;
    }
    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
    public Time getCurrentTime() {
        return currentTime;
    }
    public void setCurrentTime(Time currentTime) {
        this.currentTime = currentTime;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public static void createNewSale(CurrentSale currentSale) throws SQLException {
        insertToSql("sale","sale_id, "
                + "user_id, "
                + "outlet_id, "
                + "date, "
                + "time, "
                + "paymenttype_id, "
                + "client_id) VALUES ('"
                + currentSale.getSaleId() + "', '"
                + currentSale.getUserId() + "', '"
                + currentSale.getSaleOutletId() + "', '"
                + currentSale.getCurrentDate() + "', '"
                + currentSale.getCurrentTime() + "', '"
                + currentSale.getPaymentTypeId() + "', '"
                + currentSale.getClientId());
    }
}