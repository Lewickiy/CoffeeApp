package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;

import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.currentSaleProducts;

public class CurrentSaleSum {
    public static double currentSaleSum() {
        double total = 0.0;
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            total = total + currentSaleProduct.getSum();
        }
        return total;
    }
}