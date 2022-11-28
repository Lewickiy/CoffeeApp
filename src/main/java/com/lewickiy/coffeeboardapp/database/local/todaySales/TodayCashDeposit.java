package com.lewickiy.coffeeboardapp.database.local.todaySales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.database.connection.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;

public class TodayCashDeposit {
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
}
