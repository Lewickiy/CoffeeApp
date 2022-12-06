package com.lewickiy.coffeeboardapp.dao.query;

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
    public static ResultSet selectProductsFromNDB(Connection con) throws SQLException {
        Statement statement = con.createStatement();
        String query = "SELECT " +
                "price.price_id AS price_id, " +
                "product.product AS product, " +
                "product.description AS description, " +
                "unit.amount_unit AS number_of_unit, " +
                "unit.unit AS unit_of_measurement, " +
                "product.product_category_id AS product_category_id, " +
                "price.price AS price, " +
                "price.fix_price AS fix_price " +
                "FROM " +
                "product " +
                "JOIN " +
                "unit " +
                "ON " +
                "product.product_id = unit.product_id " +
                "JOIN " +
                "price " +
                "ON " +
                "price.unit_id = unit.unit_id " +
                "WHERE unit.outlet_id = 5 " +
                "ORDER BY product.product";
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