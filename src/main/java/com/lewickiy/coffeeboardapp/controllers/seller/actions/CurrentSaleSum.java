package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;

import static com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList.currentSaleProducts;

public class CurrentSaleSum {
    public static double currentSaleSum() {
        double total = 0.0;
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            total = currentSaleProduct.getSum();
        }
        return total;
    }
}