package com.lewickiy.coffeeboardapp.entities.paymenttype;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class PaymentTypeList {
    public static ArrayList<PaymentType> paymentTypes = new ArrayList<>();

    public static void createPaymentTypeList(Connection localCon) throws SQLException {

        ResultSet resultSet = selectAllFromSql(localCon, "local_database","paymenttype");
        while(resultSet.next()) {
            int paymentTypeId = resultSet.getInt("paymenttype_id");
            String paymentType = resultSet.getString("paymenttype");
            paymentTypes.add(new PaymentType(paymentTypeId, paymentType));
        }
        resultSet.close();
    }
}