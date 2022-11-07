package com.lewickiy.coffeeboardapp.database.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;

public class CheckShift {
    /**
     * Данный метод позволяет узнать из локальной базы данных, закрыта ли смена.
     * Для этого существует таблица shift.
     * @return - boolean значение ответа на вопрос "Закрыта ли смена?"
     * @throws SQLException - не обрабатывается
     */
    public static boolean checkShift() throws SQLException {
        boolean isClosed;
        int outletId = currentOutlet.getOutletId();
        Connection con = getConnection("local_database");
        System.out.println("START CHECK");
        String sqlSearchOutlet = "SELECT outlet_id FROM shift WHERE outlet_id = " + outletId + ";";
        Statement stm = con.createStatement();
        ResultSet rsSearchOutlet = stm.executeQuery(sqlSearchOutlet);
        if(rsSearchOutlet.getInt("outlet_id") != outletId) {
            System.out.println("OUTLET NOT FOUND");
            String sqlAddOutlet = "INSERT INTO shift (outlet_id, is_closed, cash_deposit) VALUES (" + outletId + ", 1, 0.0);";
            Statement stmSqlAddOutlet = con.createStatement();
            stmSqlAddOutlet.executeUpdate(sqlAddOutlet);
            stmSqlAddOutlet.close();
        }
        rsSearchOutlet.close();
        stm.close();

        String sql = "SELECT is_closed FROM shift WHERE outlet_id = " + outletId + ";";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        isClosed = rs.getBoolean("is_closed");

        rs.close();
        statement.close();
        con.close();

        return isClosed;
    }
}