package com.lewickiy.coffeeboardapp.dao.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;

import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesList.todaySalesArrayList;

public class TodaySalesSumCard {
    /**
     * Данный метод считает сумму всех продаж по карте Текущей(открытой) смены.
     * @return - сумму всех продаж по карте в виде double без округлений.
     */
    public static double sumCard() {
        double sumCard = 0.00;
        for (SaleProduct saleProduct : todaySalesArrayList) {
            TodaySales tempTodaySale = (TodaySales) saleProduct;
            if (tempTodaySale.getPaymentType().equals("Банковская карта")) {
                sumCard = sumCard + tempTodaySale.getSum();
            }
        }
        return sumCard;
    }
}
