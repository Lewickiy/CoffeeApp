package com.lewickiy.coffeeboardapp.database.discount;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;

public class DiscountList {
    public static ArrayList<Discount> discounts = new ArrayList<>();

    public static void createDiscountList() throws SQLException {
        Statement statement = getConnection().createStatement(); //создаётся подключение
        String query = "SELECT * FROM discount"; //создаётся запрос к базе данных
        ResultSet resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            int discountId = resultSet.getInt("discount_id");
            int discount = resultSet.getInt("discount");
            boolean active = resultSet.getBoolean("active");
            discounts.add(new Discount(discountId, discount, active));
        }

        int activeCount = 0;
        for (Discount discount : discounts) {
            if (discount.isActive() == true) {
                activeCount++;
            }
        }
    }
}
