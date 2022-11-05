package com.lewickiy.coffeeboardapp.database.product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectAllFromSql;

public class ProductCategoryList {
    public static ArrayList<ProductCategory> productCategories = new ArrayList<>();

    public static void createProductCategoriesList(Connection localCon) throws SQLException {

        ResultSet resultSet = selectAllFromSql(localCon, "local_database","product_category");
        while (resultSet.next()) {
            int productCategoryId = resultSet.getInt("PRODUCT_CATEGORY_ID");
            String productCategory = resultSet.getString("product_category");

            productCategories.add(new ProductCategory(productCategoryId, productCategory));
        }
        resultSet.close();
    }
}