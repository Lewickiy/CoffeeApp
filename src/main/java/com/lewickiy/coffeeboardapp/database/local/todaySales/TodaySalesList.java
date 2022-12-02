package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;

import java.util.ArrayList;

/**
 * Класс списка продаж Текущей(открытой) смены. Содержит todaySalesArrayList, с которым и работает.
 */
public class TodaySalesList {
    public static ArrayList<SaleProduct> todaySalesArrayList = new ArrayList<>();
}