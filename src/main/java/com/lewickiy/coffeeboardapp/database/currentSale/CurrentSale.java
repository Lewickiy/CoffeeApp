package com.lewickiy.coffeeboardapp.database.currentSale;

import com.lewickiy.coffeeboardapp.database.DatabaseConnector;
import com.lewickiy.coffeeboardapp.database.user.UserList;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class CurrentSale {
    private int saleId;
    private int userId;
    private int outletId;
    private Date currentDate;
    private Time currentTime;

    private int paymentTypeId;

    public CurrentSale(int saleId, int userId, int outletId, Date currentDate, Time currentTime) {
        this.saleId = saleId;
        this.userId = userId;
        this.outletId = outletId;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
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

    public static void createNewSale(CurrentSale currentSale) throws SQLException {
        Statement statement = DatabaseConnector.getConnection().createStatement();
        //TODO try-catch для createStatement();
        statement.executeUpdate("INSERT sale("
                + "sale_id, "
                + "user_id, "
                + "outlet_id, "
                + "date, "
                + "time) VALUES ('"
                + currentSale.getSaleId() + "', '"
                + UserList.currentUser.getUserId() + "', '"
                + currentSale.getSaleOutletId() + "', '"
                + currentSale.getCurrentDate() + "', '"
                + currentSale.getCurrentTime() + "')");
    }
}
