package com.lewickiy.coffeeboardapp.entities.product;

import com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategory;

import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;

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