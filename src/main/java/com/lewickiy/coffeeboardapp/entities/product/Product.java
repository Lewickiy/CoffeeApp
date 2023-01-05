package com.lewickiy.coffeeboardapp.entities.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Product {
    private int productId;
    private String product;
    private String description;
    private int numberOfUnit;
    private String unitOfMeasurement;
    private int category;
    private double price;
    private boolean fixPrice;

    @Override
    public String toString() {
        return productId + " - product id, "
                + product + " - product, "
                + numberOfUnit + " - number of unit, "
                + unitOfMeasurement + " - unit of measurement, "
                + category + " - category id, "
                + price + " - price, "
                + fixPrice + " - fix price.";
    }
}