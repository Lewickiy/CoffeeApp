package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;

import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesList.todaySalesArrayList;

public class LitersOfDrinks {

    /**
     * Подсчёт проданных напитков в литрах
     * @return - возвращается значение double не округлённое.
     */
    public static double countLitersOfDrinks() {
        double litres = 0.00;
        for (SaleProduct saleProduct : todaySalesArrayList) {
            TodaySales tempTodaySale = (TodaySales) saleProduct;
            if (tempTodaySale.getUnitOfMeasurement().equals("мл.")) {
                litres = litres + (tempTodaySale.getNumberOfUnit() * tempTodaySale.getAmount());
            }
        }
        return litres/1000;
    }
}
