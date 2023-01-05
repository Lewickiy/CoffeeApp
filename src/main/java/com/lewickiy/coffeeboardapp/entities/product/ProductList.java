package com.lewickiy.coffeeboardapp.entities.product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static java.util.Comparator.comparing;

public class ProductList {
    public static List<Product> products = new ArrayList<>();

    public static void createProductsList() {
        Connection con;
        ResultSet resultSet;
        try {
            con = getConnectionLDB();
            if (con != null) {
                resultSet = selectAllFromSql(con, "local_database", "product");
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    String product = resultSet.getString("product");
                    String description = resultSet.getString("description");
                    int numberOfUnit = resultSet.getInt("number_of_unit");
                    String unitOfMeasurement = resultSet.getString("unit_of_measurement");
                    int category = resultSet.getInt("product_category_id");
                    double price = resultSet.getDouble("price");
                    boolean fixPrice = decodeIntBoolean(resultSet.getInt("fix_price"));

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
            } else {
                LOGGER.log(Level.WARNING,"Lost connection to local database when creating product list");
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Exception when connecting or generating a result set when creating a list of products");
        }
    }
}