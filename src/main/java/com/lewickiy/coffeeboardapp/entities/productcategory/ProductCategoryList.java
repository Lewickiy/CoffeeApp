package com.lewickiy.coffeeboardapp.entities.productcategory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class ProductCategoryList {
    public static List<ProductCategory> productCategories = new ArrayList<>();

    public static void createProductCategoriesList() {
        Connection con;
        ResultSet resultSet;
        try {
            con = getConnectionLDB();
            if (con != null) {
                resultSet = selectAllFromSql(con, "local_database","product_category");
                while (resultSet.next()) {
                    int productCategoryId = resultSet.getInt("product_category_id");
                    String productCategory = resultSet.getString("product_category");
                    productCategories.add(new ProductCategory(productCategoryId, productCategory));
                }
                resultSet.close();
                con.close();
            } else {
                LOGGER.log(Level.WARNING,"Lost connection to local database when creating product category list");
            }
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Exception when connecting or generating a result set when creating a list of product categories");
        }
    }
}