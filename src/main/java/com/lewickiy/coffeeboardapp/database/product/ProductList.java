package com.lewickiy.coffeeboardapp.database.product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;

public class ProductList {
    public static ArrayList<Product> products = new ArrayList<>();

    public static void createProductsList() throws SQLException {
        Statement statement = getConnection().createStatement(); //создаётся подключение
        String query = "SELECT * FROM product"; //создаётся запрос к базе данных
        ResultSet resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            int productId = resultSet.getInt("product_id");
            String product = resultSet.getString("product");
            String description = resultSet.getString("description");
            int numberOfUnit = resultSet.getInt("number_of_unit");
            String unitOfMeasurement = resultSet.getString("unit_of_measurement");
            int category = resultSet.getInt("product_category_id");
            double price = resultSet.getDouble("price");

            products.add(new Product(productId
                    , product
                    , description
                    , numberOfUnit
                    , unitOfMeasurement
                    , category
                    , price));
        }
    }
}
