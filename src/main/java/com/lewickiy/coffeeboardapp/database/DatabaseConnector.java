package com.lewickiy.coffeeboardapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public class DatabaseConnector {
    private static final String DB_DRIVER = "jdbc:mysql:";
    private static final String HOST = "server5.hosting.reg.ru";
    private static final int PORT = 3306;
    private static final String DB_NAME = "u1718085_default";
    private static final String URL = DB_DRIVER + "//" + HOST + ":" + PORT + "/" + DB_NAME;
    private static final String USER = "u1718085_default";
    private static final String PASSWORD = "6RnDd5Nkk1EQGjv3";
    public static Connection connectNow = null;
    /**
     * Метод реализует подключение к базе данных<br>
     * @return - возвращает Connection connectNow; при запросе из прочих участков кода
     */
    public static Connection getConnection(String dbName) throws SQLException { // метод подключения к базе данных
        try {
            if (dbName.equals("network_database")) {
                connectNow = DriverManager.getConnection(URL, USER, PASSWORD);
            } else if (dbName.equals("local_database")) {
                connectNow = DriverManager.getConnection("jdbc:sqlite:coffeeapp_local.db"); //Заменить на переменную

            }
        } catch (SQLException sqlEx) {

        }
        return connectNow;
    }
}