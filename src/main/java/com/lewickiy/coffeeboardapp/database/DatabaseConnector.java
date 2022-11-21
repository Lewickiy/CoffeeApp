package com.lewickiy.coffeeboardapp.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseConnector {
    /**
     * Метод реализует подключение к базе данных<br>
     * @return - возвращает Connection connectNow; при запросе из прочих участков кода
     */
    public static Connection getConnection(String dbName) throws SQLException { // метод подключения к базе данных
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))){
            props.load(in);
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");
        Connection con = null;
        if (dbName.equals("network_database")) {
            con = DriverManager.getConnection(url, username, password);
        } else if (dbName.equals("local_database")) {
            con = DriverManager.getConnection("jdbc:sqlite:coffeeapp_local.db");
        }
        return con;
    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}