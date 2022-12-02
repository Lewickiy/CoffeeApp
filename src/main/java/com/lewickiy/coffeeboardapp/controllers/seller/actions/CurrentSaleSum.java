package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;

import static com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProductList.currentSaleProducts;

public class CurrentSaleSum {
    public static double currentSaleSum() {
        double total = 0.0;
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            total = currentSaleProduct.getSum();
        }
        return total;
    }
}