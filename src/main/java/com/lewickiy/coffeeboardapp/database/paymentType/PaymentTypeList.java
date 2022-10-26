package com.lewickiy.coffeeboardapp.database.paymentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectAllFromSql;

public class PaymentTypeList {
    public static ArrayList<PaymentType> paymentTypes = new ArrayList<>();

    public static void createPaymentTypeList() throws SQLException {
        ResultSet resultSet = selectAllFromSql("local_database","paymenttype");
        while(resultSet.next()) {
            int paymentTypeId = resultSet.getInt("paymenttype_id");
            String paymentType = resultSet.getString("paymenttype");
            paymentTypes.add(new PaymentType(paymentTypeId, paymentType));
        }
    }
}