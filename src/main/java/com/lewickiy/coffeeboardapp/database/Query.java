package com.lewickiy.coffeeboardapp.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;

public class Query {
    /**
     * Запрос SELECT FROM к таблице sql.
     * @param tableName - принимаемый параметр типа String - Имя таблицы, к которой обращаемся.
     * @return - метод возвращает объект resultSet класса ResultSet.
     * @throws SQLException - TODO проработать, описать.
     */
    public static ResultSet selectAllFromSql(String dbName, String tableName) throws SQLException {
        System.out.println("Start connection to " + dbName + " table: " + tableName);
        Statement statement = getConnection(dbName).createStatement(); //создаётся подключение
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

    public static void deleteFromSql(String dbName, String tableName, String action) throws SQLException {
        action = action.toUpperCase();
        String query = action + " FROM " + tableName;
        System.out.println("CONNECTION TO " + dbName + " for DELETING all data FROM " + tableName);
        Statement statement = getConnection(dbName).createStatement(); //создаётся подключение
        statement.executeUpdate(query);
        System.out.println("DATA from " + tableName + " DELETED");
    }

    public static Statement insertToSql(String dbName, String tableName, String sql) throws SQLException {
        Statement statement = getConnection(dbName).createStatement();
        if (dbName.equals("network_database")) {
            statement.executeUpdate("INSERT " + tableName + "(" + sql + "')");
        } else if (dbName.equals("local_database")) {
            statement.executeUpdate("INSERT INTO " + tableName + "(" + sql + ")");
        }
        return statement;
    }
}