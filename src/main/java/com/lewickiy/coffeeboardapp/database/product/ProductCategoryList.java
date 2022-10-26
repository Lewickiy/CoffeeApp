package com.lewickiy.coffeeboardapp.database.product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectAllFromSql;

public class ProductCategoryList {
    public static ArrayList<ProductCategory> productCategories = new ArrayList<>();

    public static void createProductCategoriesList() throws SQLException {
        ResultSet resultSet = selectAllFromSql("local_database","product_category");
        while (resultSet.next()) {
            int productCategoryId = resultSet.getInt("product_category_id");
            String productCategory = resultSet.getString("product_category");

            productCategories.add(new ProductCategory(productCategoryId, productCategory));
        }
    }
}