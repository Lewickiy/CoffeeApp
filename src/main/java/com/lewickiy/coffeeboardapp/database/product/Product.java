package com.lewickiy.coffeeboardapp.database.product;

public class Product {
    private int productId;
    private String product;
    private String description;
    private int numberOfUnit;
    private String unitOfMeasurement;
    private int category;
    private double price;

    public Product(int productId
            , String product
            , String description
            , int numberOfUnit
            , String unitOfMeasurement
            , int category
            , double price) {
        this.productId = productId;
        this.product = product;
        this.description = description;
        this.numberOfUnit = numberOfUnit;
        this.unitOfMeasurement = unitOfMeasurement;
        this.category = category;
        this.price = price;
    }
    public Product(String product
            , String description
            , int numberOfUnit
            , String unitOfMeasurement
            , int category
            , double price) {
        this.product = product;
        this.description = description;
        this.numberOfUnit = numberOfUnit;
        this.unitOfMeasurement = unitOfMeasurement;
        this.category = category;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfUnit() {
        return numberOfUnit;
    }

    public void setNumberOfUnit(int numberOfUnit) {
        this.numberOfUnit = numberOfUnit;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
