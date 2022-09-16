package com.lewickiy.coffeeboardapp.database.paymentType;

import com.lewickiy.coffeeboardapp.database.product.Product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;

public class PaymentTypeList {
    public static ArrayList<PaymentType> paymentTypes = new ArrayList<>();

    public static void createPaymentTypeAL() throws SQLException {
        Statement statement = getConnection().createStatement(); //создаётся подключение
        String query = "SELECT * FROM paymenttype"; //создаётся запрос к базе данных
        ResultSet resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            int paymentTypeId = resultSet.getInt("paymenttype_id");
            String paymentType = resultSet.getString("paymenttype");
            paymentTypes.add(new PaymentType(paymentTypeId, paymentType));
        }
    }
}
