package com.lewickiy.coffeeboardapp.dao.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;

import java.util.ArrayList;

/**
 * Класс списка продаж Текущей(открытой) смены. Содержит todaySalesArrayList, с которым и работает.
 */
public class TodaySalesList {
    public static ArrayList<SaleProduct> todaySalesArrayList = new ArrayList<>();
}