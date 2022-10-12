package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;

import java.sql.Time;
import java.util.ArrayList;

public class TodaySalesList {

    public static ArrayList<SaleProduct> todaySalesArrayList = new ArrayList<>();

    public static void addCurrentSaleToArray(ArrayList<SaleProduct> currentSaleProducts, CurrentSale currentSale) {
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            TodaySales tempSale = new TodaySales(currentSale.getSaleId(), currentSaleProduct.getProductId(), currentSaleProduct.getProduct(), currentSaleProduct.getPrice(), currentSaleProduct.getDiscountId(), currentSaleProduct.getDiscount(), currentSaleProduct.getAmount(), currentSaleProduct.getSum());
            long nowTime = System.currentTimeMillis();
            Time saleTime = new Time(nowTime);
            tempSale.setSaleTime(saleTime);
            todaySalesArrayList.add(tempSale);
            tempSale = null;
        }
    }
}
