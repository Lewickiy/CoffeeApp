package com.lewickiy.coffeeboardapp.database.product;

import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.products;

public class ProductsInCategory {
    public static void countingProductsInCategory() {
        for (Product product : products) {
            for (ProductCategory productCategory : productCategories) {
                if (product.getCategory() == productCategory.getProductCategoryId()) {
                    productCategory.setAmountProducts(productCategory.getAmountProducts() + 1);
                }
            }
        }
    }
}
