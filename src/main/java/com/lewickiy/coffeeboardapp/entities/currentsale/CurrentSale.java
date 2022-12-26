package com.lewickiy.coffeeboardapp.entities.currentsale;

import com.lewickiy.coffeeboardapp.dao.connector.DataBaseEnum;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;
@Getter
@Setter
@RequiredArgsConstructor
public class CurrentSale {
    @NonNull
    private long saleId;
    @NonNull
    private int userId;
    @NonNull
    private int outletId;
    private Date currentDate;
    private Time currentTime;
    private int paymentTypeId;
    private int clientId;
    private boolean loaded;

    public static void addToLocalDB(CurrentSale currentSale) {
        insertToSql(DataBaseEnum.LOCAL_DB, "sale","sale_id, "
                + "user_id, "
                + "outlet_id, "
                + "date, "
                + "time, "
                + "paymenttype_id, "
                + "client_id, "
                + "loaded) VALUES ('"
                + currentSale.getSaleId() + "', '"
                + currentSale.getUserId() + "', '"
                + currentSale.getOutletId() + "', '"
                + currentSale.getCurrentDate() + "', '"
                + currentSale.getCurrentTime() + "', '"
                + currentSale.getPaymentTypeId() + "', '"
                + currentSale.getClientId()  + "', '"
                + 0 + "'");
    }
}