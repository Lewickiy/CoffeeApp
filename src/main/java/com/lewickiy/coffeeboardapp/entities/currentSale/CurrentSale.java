package com.lewickiy.coffeeboardapp.entities.currentSale;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

import static com.lewickiy.coffeeboardapp.database.connection.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.Query.insertToSql;

public class CurrentSale {
    private int saleId;
    private int userId;
    private int outletId;
    private Date currentDate;
    private Time currentTime;
    private int paymentTypeId;
    private int clientId;

    private boolean loaded;

    public CurrentSale(int saleId, int userId, int outletId, Date currentDate, Time currentTime, int paymentTypeId, int clientId, boolean loaded) {
        this.saleId = saleId;
        this.userId = userId;
        this.outletId = outletId;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.paymentTypeId = paymentTypeId;
        this.clientId = clientId;
        this.loaded = loaded;
    }

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

    public int getOutletId() {
        return outletId;
    }

    public void setOutletId(int outletId) {
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

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public static void addSaleToLocalDB(CurrentSale currentSale) throws SQLException {
        Connection con = getConnection("local_database");
        insertToSql(con, "local_database", "sale","sale_id, "
                + "user_id, "
                + "outlet_id, "
                + "date, "
                + "time, "
                + "paymenttype_id, "
                + "client_id, "
                + "loaded) VALUES ('"
                + currentSale.getSaleId() + "', '"
                + currentSale.getUserId() + "', '"
                + currentSale.getSaleOutletId() + "', '"
                + currentSale.getCurrentDate() + "', '"
                + currentSale.getCurrentTime() + "', '"
                + currentSale.getPaymentTypeId() + "', '"
                + currentSale.getClientId()  + "', '"
                + 0 + "'");
        con.close();
    }
}