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
        Statement statement = con.createStatement(); //создаётся подключение
        String query = null;
        if (dbName.equals("network_database")) {
            query = "SELECT * FROM " + tableName; //создаётся запрос к базе данных
        } else if (dbName.equals("local_database")) {
            query = "SELECT * FROM " + tableName + ";";
        }
        return statement.executeQuery(query);
    }

    public static void deleteFromSql(Connection con, String dbName, String tableName, String action) throws SQLException {
        action = action.toUpperCase();
        String query = action + " FROM " + tableName;
        Statement statement = con.createStatement(); //создаётся подключение
        statement.executeUpdate(query);
        statement.close();
    }

    public static void showTablesFromSql(Connection con, String dbName, String tableName, String action) throws SQLException {
        action = action.toUpperCase();
        String query = action + " tables";
        Statement statement = con.createStatement(); //создаётся подключение
        ResultSet rs = statement.executeQuery(query);
        while(rs.next()) {
            //TODO
        }
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
