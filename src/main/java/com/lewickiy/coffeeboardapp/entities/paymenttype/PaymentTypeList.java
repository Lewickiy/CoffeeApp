package com.lewickiy.coffeeboardapp.entities.paymenttype;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class PaymentTypeList {
    public static List<PaymentType> paymentTypes = new ArrayList<>();

    public static void createPaymentTypeList() {
        Connection con;
        ResultSet resultSet;

        try {
            con = getConnectionLDB();

            if (con != null) {
                resultSet = selectAllFromSql(con, "local_database","paymenttype");

                while(resultSet.next()) {
                    int paymentTypeId = resultSet.getInt("paymenttype_id");
                    String paymentType = resultSet.getString("paymenttype");
                    paymentTypes.add(new PaymentType(paymentTypeId, paymentType));
                }
                resultSet.close();
                con.close();

            } else {
                LOGGER.log(Level.WARNING,"Lost connection to local database when creating payment type list");
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Exception when connecting or generating a result set when creating a list of payment types");
        }
    }
}