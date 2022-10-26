package com.lewickiy.coffeeboardapp.database.discount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectAllFromSql;

public class DiscountList {
    public static ArrayList<Discount> discounts = new ArrayList<>();

    public static void createDiscountList() throws SQLException {
        ResultSet resultSet = selectAllFromSql("network_database", "discount");

        while(resultSet.next()) {
            int discountId = resultSet.getInt("discount_id");
            int discount = resultSet.getInt("discount");
            boolean active = resultSet.getBoolean("active");
            discounts.add(new Discount(discountId, discount, active));
        }
    }
}