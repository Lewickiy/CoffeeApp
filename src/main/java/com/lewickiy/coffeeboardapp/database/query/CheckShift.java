package com.lewickiy.coffeeboardapp.database.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;

public class CheckShift {
    public static boolean checkShift() throws SQLException {
        System.out.println();
        System.out.println("______________________________");
        System.out.println("Start checking SHIFT status...");
        boolean isClosed;
        int outletId = currentOutlet.getOutletId();
        Connection con = getConnection("local_database");

        String sql = "SELECT is_closed FROM shift WHERE outlet_id = " + outletId + ";";
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        isClosed = rs.getBoolean("is_closed");
        System.out.println("SHIFT is closed? - " + isClosed);
        rs.close();
        statement.close();
        con.close();
        System.out.println();
        return isClosed;
    }
}