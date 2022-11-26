package com.lewickiy.coffeeboardapp.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;

public class Query {

    public static ResultSet selectAllFromSql(Connection con, String dbName, String tableName) throws SQLException {
        Statement statement = con.createStatement();
        String query = null;
        if (dbName.equals("network_database")) {
            query = "SELECT * FROM " + tableName;
        } else if (dbName.equals("local_database")) {
            query = "SELECT * FROM " + tableName + ";";
        }
        return statement.executeQuery(query);
    }

    public static void deleteFromLocalSql(Connection con, String tableName) throws SQLException {
        LOGGER.log(Level.INFO,"Start clear " + tableName + "...");
        String query = "DELETE FROM " + tableName;
        Statement statement = con.createStatement();
        statement.executeUpdate(query);
        LOGGER.log(Level.INFO,tableName + " cleared");
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