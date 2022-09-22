package com.lewickiy.coffeeboardapp.database.product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectFromSql;

public class ProductList {
    public static ArrayList<Product> products = new ArrayList<>();

    public static void createProductsList() throws SQLException {
        ResultSet resultSet = selectFromSql("product");

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