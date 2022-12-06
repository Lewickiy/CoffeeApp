package com.lewickiy.coffeeboardapp.dao.connector;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static com.lewickiy.coffeeboardapp.dao.connector.Database.*;

public class DatabaseConnector {
    public static Connection getConnection(Database database) throws SQLException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get("database.properties"))){
            props.load(in);
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");
        Connection con = null;
        if (database.equals(NETWORK_DB)) {
            con = DriverManager.getConnection(url, username, password);
        } else if (database.equals(LOCAL_DB)) {
            con = DriverManager.getConnection("jdbc:sqlite:coffeeapp_local.db");
        }
        return con;
    } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}