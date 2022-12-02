package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;

import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesList.todaySalesArrayList;

public class TodaySalesSumAll {
    /**
     * Данный метод считает сумму всех продаж Текущей(открытой) смены.
     * @return - сумму всех продаж в виде double без округлений.
     */
    public static double sumAll() { //сумма всех продаж
        double sumAll = 0.00;
        for (SaleProduct saleProduct : todaySalesArrayList) {
            sumAll = sumAll + saleProduct.getSum();
        }
        return sumAll;
    }
}
