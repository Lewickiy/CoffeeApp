package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.paymentType.PaymentType;

import java.sql.*;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.paymentTypes;

public class TodaySalesList { //Список сегодняшних продаж
    public static ArrayList<SaleProduct> todaySalesArrayList = new ArrayList<>();

    //Данный метод считает все продажи сегодняшнего дня (сумму)
    public static double sumAll() { //сумма всех продаж
        double sumAll = 0.00;
        for (SaleProduct saleProduct : todaySalesArrayList) {
            sumAll = sumAll + saleProduct.getSum();
        }
        return sumAll;
    }

    //Данный метод считает все продажи сегодняшнего дня за наличные.
    public static double sumCash() {
        double sumCash = 0.00;
        for (int i = 0; i < todaySalesArrayList.size(); i++) {
            TodaySales tempTodaySale = (TodaySales) todaySalesArrayList.get(i);
            if (tempTodaySale.getPaymentType().equals("Наличные деньги")) {
                    sumCash = sumCash + tempTodaySale.getSum();
            }
        }
        return sumCash;
    }

    //Данный метод считает все продажи сегодняшнего дня по банковской карте.
    public static double sumCard() {
        double sumCard = 0.00;
        for (int i = 0; i < todaySalesArrayList.size(); i++) {
            TodaySales tempTodaySale = (TodaySales) todaySalesArrayList.get(i);
            if (tempTodaySale.getPaymentType().equals("Банковская карта")) {
                sumCard = sumCard + tempTodaySale.getSum();
            }
        }
        return sumCard;
    }

    /**
     * Данный метод добавляет сформированный объект tempSale класса TodaySales в Array todaySalesArrayList
     * для этого он принимает следующие параметры:
     * @param currentSaleProducts - ArrayList продуктов текущей продажи
     * @param currentSale - объект класса CurrentSale.
     * TODO для того чтобы при перезагрузке системы не происходил сброс этого списка,
     *                    он должен формироваться отдельно в базе данных. Пока это локальная база данных.
     *                    То есть, данный метод должен принимать параметры чего-то в базе данных.
     *
     */
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
    //Метод, который опрашивает локальную базу данных через join. Заготовка для таблицы сегодняшних продаж.
    public static void addCurrentSaleToArray2(Connection con, String query) throws SQLException {
        System.out.println("Start loading Sales List");
        Statement statement = con.createStatement(); //создаётся подключение
        query = "SELECT sale.time, product.product, product.number_of_unit, product.unit_of_measurement, sale_product.price, sale_product.amount, discount.discount, sale_product.sum, paymenttype.paymenttype FROM sale FULL OUTER JOIN sale_product ON sale.sale_id = sale_product.sale_id FULL OUTER JOIN paymenttype ON sale.paymenttype_id = paymenttype.paymenttype_id FULL OUTER JOIN discount ON sale_product.discount_id = discount.discount_id FULL OUTER JOIN product ON sale_product.product_id = product.product_id";
        ResultSet rs = statement.executeQuery(query);
        int count = 0;

        while(rs.next()) {
            if (rs.getString(1) == null) {
                break;
            } else {
                count++;
                System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3) + " | " + rs.getString(4) + " | " + rs.getString(5) + " | " + rs.getString(6) + " | " + rs.getString(7) + " | " + rs.getString(8) + " | " + rs.getString(9));
            }
        }
        System.out.println(count);
    }
}