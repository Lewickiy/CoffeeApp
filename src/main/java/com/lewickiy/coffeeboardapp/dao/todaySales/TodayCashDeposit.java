package com.lewickiy.coffeeboardapp.dao.todaySales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class TodayCashDeposit {
    /**
     * Данный метод, подключаясь к локальной базе данных, берёт сумму Депозита наличными,
     * внесённую при открытии текущей смены
     * @return - эта сумма возвращается в виде double для дальнейших операций с ней.
     * Например: данная сумма прибавляется к сумме продаж за наличный расчёт, чтобы показать,
     * сколько фактически наличных денег находится в кассе.
     * TODO обработка исключений при отсутствии соединения с локальной базой данных в виде информационных сообщений с вариантами решения проблемы.
     */
    public static double getCashDeposit() {
        String sql = "SELECT cash_deposit FROM shift WHERE outlet_id = " + currentOutlet.getOutletId() + ";";
        double cashDeposit = 0.0;

        Connection con;
        Statement statement;
        ResultSet rs;

        try {
            con = getConnectionLDB();
            if (con != null) {
                statement = con.createStatement();
                rs = statement.executeQuery(sql);
                cashDeposit = rs.getDouble("cash_deposit");
                rs.close();
                statement.close();
                con.close();
            } else {
                LOGGER.log(Level.WARNING, "Connection to LocalDatabase for getting CashDeposit failed");
            }
            return cashDeposit;
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING, "getCashDeposit error");
            return cashDeposit;
        }
    }
}