package com.lewickiy.coffeeboardapp.entities.currentsale;

import com.lewickiy.coffeeboardapp.dao.connector.Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;

public class CurrentSale {
    private long saleId;
    private int userId;
    private int outletId;
    private Date currentDate;
    private Time currentTime;
    private int paymentTypeId;
    private int clientId;

    private boolean loaded;

    public CurrentSale(long saleId, int userId, int outletId) {
        this.saleId = saleId;
        this.userId = userId;
        this.outletId = outletId;
    }

    public long getSaleId() {
        return saleId;
    }
    public void setSaleId(long saleId) {
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
        Connection con = getConnection(Database.LOCAL_DB);
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