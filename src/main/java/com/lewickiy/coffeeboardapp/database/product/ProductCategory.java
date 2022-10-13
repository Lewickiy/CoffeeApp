package com.lewickiy.coffeeboardapp.database.product;

public class ProductCategory {
    private int productCategoryId;
    private String productCategory;
    private int amountProducts;

    public ProductCategory(int productCategoryId, String productCategory) {
        this.productCategoryId = productCategoryId;
        this.productCategory = productCategory;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public int getAmountProducts() {
        return amountProducts;
    }

    public void setAmountProducts(int amountProducts) {
        this.amountProducts = amountProducts;
    }
}