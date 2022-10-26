package com.lewickiy.coffeeboardapp.controllers;

import java.sql.*;

public class Tests {
    public static void showSalesThisShift() throws SQLException {
        Connection conn = null; //Подключение null
        try {
            // Параметры базы данных
            String url = "jdbc:sqlite:coffeeapp_local.db";
//            // создаём подключение к базе данных
            conn = DriverManager.getConnection(url);
            Statement statement = conn.createStatement(); //создаётся подключение
//            String queryInsert = "INSERT into outlet VALUES (12, 'Hell');";
//            statement.executeUpdate(queryInsert);
            //---------------------------------


            //---------------or----------------
            String query = "SELECT * FROM outlet"; //создаётся запрос к базе данных
            ResultSet rs = statement.executeQuery(query); //результат
            System.out.println("Query is ok");
            while (rs.next()) {
                System.out.println(rs.getInt("outlet_id") + "\t" +
                        rs.getString("outlet"));

            }} catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
