package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;

/**
 * Класс списка продаж Текущей(открытой) смены. Содержит todaySalesArrayList, с которым и работает.
 * А также методы для работы со списком сегодняшних продаж.
 */
public class TodaySalesList {
    public static ArrayList<SaleProduct> todaySalesArrayList = new ArrayList<>();
    /**
     * Подсчёт проданных напитков в литрах
     * @return - возвращается значение double не округлённое.
     */
    public static double litresSum() {
        double litres = 0.00;
        for (SaleProduct saleProduct : todaySalesArrayList) {
            TodaySales tempTodaySale = (TodaySales) saleProduct;
            if (tempTodaySale.getUnitOfMeasurement().equals("мл.")) {
                litres = litres + (tempTodaySale.getNumberOfUnit() * tempTodaySale.getAmount());
            }
        }
        return litres/1000;
    }
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

    /**
     * Данный метод, подключаясь к локальной базе данных, берёт сумму Депозита наличными,
     * внесённую при открытии текущей смены
     * @return - эта сумма возвращается в виде double для дальнейших операций с ней.
     * Например: данная сумма прибавляется к сумме продаж за наличный расчёт, чтобы показать,
     * сколько фактически наличных денег находится в кассе.
     * @throws SQLException - исключение пока не обрабатывается.
     * TODO обработка исключений при отсутствии соединения с локальной базой данных в виде информационных сообщений с вариантами решения проблемы.
     */
    public static double getCashDeposit() throws SQLException {
        double cashDeposit;
        Connection con = getConnection("local_database");
        String sql = "SELECT cash_deposit FROM shift WHERE outlet_id = " + currentOutlet.getOutletId() + ";";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        cashDeposit = rs.getDouble("cash_deposit");
        return cashDeposit;
    }

    /**
     * //Метод, который опрашивает локальную базу данных через join. Заготовка для таблицы сегодняшних продаж.
     * @param con - принимает Connection
     * @throws SQLException - не обрабатывается.
     */
    public static void addAllSalesToArray(Connection con) throws SQLException {
        System.out.println("Start loading Sales List");
        Statement statement = con.createStatement(); //создаётся подключение
        String query = "SELECT sale.sale_id" +
                ", sale.time" +
                ", product.product_id" +
                ", product.product" +
                ", product.number_of_unit" +
                ", product.unit_of_measurement" +
                ", sale_product.price" +
                ", sale_product.amount" +
                ", discount.discount_id" +
                ", discount.discount" +
                ", sale_product.sum" +
                ", paymenttype.paymenttype" +
                " FROM sale " +
                "FULL OUTER JOIN sale_product ON sale.sale_id = sale_product.sale_id " +
                "FULL OUTER JOIN paymenttype ON sale.paymenttype_id = paymenttype.paymenttype_id " +
                "FULL OUTER JOIN discount ON sale_product.discount_id = discount.discount_id " +
                "FULL OUTER JOIN product ON sale_product.product_id = product.product_id";
        ResultSet rs = statement.executeQuery(query);
        //TODO добавить время продажи
        while(rs.next()) {
            if (rs.getString(1) == null) {
                break;
            } else {
                TodaySales tempSale = new TodaySales(rs.getInt(1) //sale id
                        , rs.getInt(3) //product id
                        , rs.getString(4) //product
                        , rs.getDouble(7) //price
                        , rs.getInt(9) //discount id
                        , rs.getInt(10) //discount
                        , rs.getInt(8) //amount
                        , rs.getDouble(11)); //sum
                tempSale.setNumberOfUnit(rs.getInt(5));
                tempSale.setUnitOfMeasurement(rs.getString(6));
                tempSale.setPaymentType(rs.getString(12));
                todaySalesArrayList.add(tempSale);
                System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3) + " | " + rs.getString(4) + " | " + rs.getString(5) + " | " + rs.getString(6) + " | " + rs.getString(7) + " | " + rs.getString(8) + " | " + rs.getString(9) + " | " + rs.getString(10) + " | " + rs.getString(11) + " | " + rs.getString(12));
            }
        }
        rs.close();
        statement.close();
    }
}