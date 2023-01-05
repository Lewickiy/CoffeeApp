package com.lewickiy.coffeeboardapp.entities.discount;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class DiscountList {
    public static List<Discount> discounts = new ArrayList<>();

    public static void createDiscountList() {
        Connection con;
        ResultSet resultSet;
        try {
            con = getConnectionLDB();
            if (con != null) {
                resultSet = selectAllFromSql(con,"local_database", "discount");
                while(resultSet.next()) {
                    int discountId = resultSet.getInt("discount_id");
                    int discount = resultSet.getInt("discount");
                    boolean active = resultSet.getBoolean("active");
                    discounts.add(new Discount(discountId, discount, active));
                }
                resultSet.close();
                con.close();
            } else {
                LOGGER.log(Level.WARNING,"Lost connection to local database when creating discounted list");
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Exception when connecting or generating a result set when creating a list of discounts");
        }
    }
}