package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.DataBaseEnum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;

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

    public static void deleteFromLocalSql(String tableName) {
        String query = "DELETE FROM " + tableName;

        Connection con = getConnectionLDB();

        if (con != null) {
            Statement statement;
            try {
                statement = con.createStatement();
                statement.executeUpdate(query);
                LOGGER.log(Level.INFO,tableName + " cleared");
                statement.close();
                con.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING,tableName + "not cleared. Connection error (" + e + ")");
            }
        } else {
            LOGGER.log(Level.WARNING,tableName + "Not cleared. Connection error");
        }
    }
    public static void insertToSql(DataBaseEnum dbEnum, String tableName, String sql) {
        Connection con;
        Statement statement;
        if (dbEnum.getDbName().equals("network_database")) {
            try {
                con = getConnectionNDB();
                statement = Objects.requireNonNull(con).createStatement();
                statement.executeUpdate("INSERT " + tableName + "(" + sql + ")");
                statement.close();
                con.close();
            } catch (SQLException sqlEx) {
                LOGGER.log(Level.WARNING,"Insert to " + tableName + "(" + dbEnum.getDbName() + ") failed. Connection or statement error");
            }
        } else if (dbEnum.getDbName().equals("local_database")) {
            try {
                con = getConnectionLDB();
                statement = Objects.requireNonNull(con).createStatement();
                statement.executeUpdate("INSERT INTO " + tableName + "(" + sql + ")");
                statement.close();
                con.close();
            } catch (SQLException sqlEx) {
                LOGGER.log(Level.WARNING,"Insert to " + tableName + "(" + dbEnum.getDbName() + ") failed. Connection or statement error");
            }
        }
    }
}