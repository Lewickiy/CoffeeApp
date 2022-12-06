package com.lewickiy.coffeeboardapp.entities.discount;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class DiscountList {
    public static ArrayList<Discount> discounts = new ArrayList<>();

    public static void createDiscountList(Connection con) throws SQLException {

        ResultSet resultSet = selectAllFromSql(con,"local_database", "discount");

        while(resultSet.next()) {
            int discountId = resultSet.getInt("discount_id");
            int discount = resultSet.getInt("discount");
            boolean active = resultSet.getBoolean("active");
            discounts.add(new Discount(discountId, discount, active));
        }
        resultSet.close();
    }
}