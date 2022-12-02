package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;

import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesList.todaySalesArrayList;

public class TodaySalesSumCash {
    /**
     * Данный метод считает сумму всех продаж за наличные Текущей(открытой) смены.
     * @return - сумму всех продаж за наличные в виде double без округлений.
     */
    public static double sumCash() {
        double sumCash = 0.00;
        for (SaleProduct saleProduct : todaySalesArrayList) {
            TodaySales tempTodaySale = (TodaySales) saleProduct;
            if (tempTodaySale.getPaymentType().equals("Наличные деньги")) {
                sumCash = sumCash + tempTodaySale.getSum();
            }
        }
        return sumCash;
    }
}
