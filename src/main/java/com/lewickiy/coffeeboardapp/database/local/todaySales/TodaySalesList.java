package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.paymentType.PaymentType;

import java.sql.Time;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.paymentTypes;

public class TodaySalesList {
    public static ArrayList<SaleProduct> todaySalesArrayList = new ArrayList<>();

    public static void addCurrentSaleToArray(ArrayList<SaleProduct> currentSaleProducts, CurrentSale currentSale) {
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            TodaySales tempSale = new TodaySales(currentSale.getSaleId()
                    , currentSaleProduct.getProductId()
                    , currentSaleProduct.getProduct()
                    , currentSaleProduct.getPrice()
                    , currentSaleProduct.getDiscountId()
                    , currentSaleProduct.getDiscount()
                    , currentSaleProduct.getAmount()
                    , currentSaleProduct.getSum());

            for (PaymentType paymentType : paymentTypes) {
                if (currentSale.getPaymentTypeId() == paymentType.getPaymentTypeId()) {
                    tempSale.setPaymentType(paymentType.getPaymentType());
                }
            }
            long nowTime = System.currentTimeMillis();
            Time saleTime = new Time(nowTime);
            tempSale.setSaleTime(saleTime);
            todaySalesArrayList.add(tempSale);
        }
    }
}