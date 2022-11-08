package com.lewickiy.coffeeboardapp.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Query {
    /**
     * Запрос SELECT FROM к таблице sql.
     * @param tableName - принимаемый параметр типа String - Имя таблицы, к которой обращаемся.
     * @return - метод возвращает объект resultSet класса ResultSet.
     * @throws SQLException - TODO проработать, описать.
     */
    public static ResultSet selectAllFromSql(Connection con, String dbName, String tableName) throws SQLException {
        System.out.println("Start connection to " + dbName + " table: " + tableName);
        Statement statement = con.createStatement(); //создаётся подключение
        System.out.println("Connected to " + dbName + " table: " + tableName);
        String query = null;
        if (dbName.equals("network_database")) {
            query = "SELECT * FROM " + tableName; //создаётся запрос к базе данных
        } else if (dbName.equals("local_database")) {
            query = "SELECT * FROM " + tableName + ";";
        }
        System.out.println("Show all data from " + dbName);
        return statement.executeQuery(query);
    }

    public static void deleteFromSql(Connection con, String dbName, String tableName, String action) throws SQLException {
        action = action.toUpperCase();
        String query = action + " FROM " + tableName;
        System.out.println("CONNECTION TO " + dbName + " for DELETING all data FROM " + tableName);
        Statement statement = con.createStatement(); //создаётся подключение
        statement.executeUpdate(query);
        System.out.println("DATA from " + tableName + " DELETED");
        statement.close();
    }

    public static void showTablesFromSql(Connection con, String dbName, String tableName, String action) throws SQLException {
        action = action.toUpperCase();
        String query = action + " tables";
        System.out.println("CONNECTION TO " + dbName + " for show all tables FROM DB");
        Statement statement = con.createStatement(); //создаётся подключение
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()) {
            System.out.print(rs.getString(1) + " | ");
        }
        System.out.println();
        statement.close();
    }

    public static void insertToSql(Connection con, String dbName, String tableName, String sql) throws SQLException {
        Statement statement = con.createStatement();
        if (dbName.equals("network_database")) {
            statement.executeUpdate("INSERT " + tableName + "(" + sql + ")");
        } else if (dbName.equals("local_database")) {
            statement.executeUpdate("INSERT INTO " + tableName + "(" + sql + ")");
        }
        statement.close();
    }
}