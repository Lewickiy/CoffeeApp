package com.lewickiy.coffeeboardapp.dao.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LDBConnector {
    public static Connection getConnectionLDB() {
        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:coffeeapp_local.db");
        } catch (SQLException e) {
            return null;
        }
        return con;
    }
}