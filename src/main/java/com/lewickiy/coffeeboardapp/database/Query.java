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
     * @throws SQLException
     */
    public static ResultSet selectFromSql(String tableName) throws SQLException {
        Statement statement = getConnection().createStatement(); //создаётся подключение
        String query = "SELECT * FROM " + tableName; //создаётся запрос к базе данных
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }

    public static Statement insertToSql(String tableName, String sql) throws SQLException {
        Statement statement = getConnection().createStatement();
        statement.executeUpdate("INSERT " + tableName + "(" + sql + "')");
        return statement;
    }
}
