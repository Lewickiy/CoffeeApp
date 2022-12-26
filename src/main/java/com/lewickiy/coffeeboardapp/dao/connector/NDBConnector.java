package com.lewickiy.coffeeboardapp.dao.connector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.connectionToNDB;

public class NDBConnector {
    public static Connection getConnectionNDB() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))) {
            props.load(in);
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");
            try {
                connectionToNDB = true;
                return DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                connectionToNDB = false;
                System.out.println("getConnectionNDB " + e);
                //TODO LOG
                return null;
            }
        } catch (IOException e) {
            //TODO LOG
            return null;
        }
    }
}