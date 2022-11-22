package com.lewickiy.coffeeboardapp.database.product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectAllFromSql;
import static java.util.Comparator.comparing;

public class ProductList {
    public static ArrayList<Product> products = new ArrayList<>();

    public static void createProductsList(Connection localCon) throws SQLException {


        ResultSet resultSet = selectAllFromSql(localCon, "local_database","product");

        while(resultSet.next()) {
            int productId = resultSet.getInt("product_id");
            String product = resultSet.getString("product");
            String description = resultSet.getString("description");
            int numberOfUnit = resultSet.getInt("number_of_unit");
            String unitOfMeasurement = resultSet.getString("unit_of_measurement");
            int category = resultSet.getInt("product_category_id");
            double price = resultSet.getDouble("price");
            boolean fixPrice;
            if (resultSet.getInt("fix_price") == 0) {
                fixPrice = false;
            } else {
                fixPrice = true;
            }

            products.add(new Product(productId
                    , product
                    , description
                    , numberOfUnit
                    , unitOfMeasurement
                    , category
                    , price
                    , fixPrice));
        }
        resultSet.close();
        products.sort(comparing(Product::getCategory));
    }
}