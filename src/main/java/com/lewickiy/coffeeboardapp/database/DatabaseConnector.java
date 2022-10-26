package com.lewickiy.coffeeboardapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String DB_DRIVER = "jdbc:mysql:";
    private static final String HOST = "server5.hosting.reg.ru";
    private static final int PORT = 3306;
    private static final String DB_NAME = "u1718085_default";
    private static final String URL = DB_DRIVER + "//" + HOST + ":" + PORT + "/" + DB_NAME;
    private static final String USER = "u1718085_default";
    private static final String PASSWORD = "6RnDd5Nkk1EQGjv3";
    public static Connection connectNow;
    /**
     * Метод реализует подключение к базе данных<br>
     * @return - возвращает Connection connectNow; при запросе из прочих участков кода
     */
    public static Connection getConnection(String dbName) { // метод подключения к базе данных
        try {
            if (dbName.equals("network_database")) {
                connectNow = DriverManager.getConnection(URL, USER, PASSWORD);
            } else if (dbName.equals("local_database")) {
                connectNow = DriverManager.getConnection("jdbc:sqlite:coffeeapp_local.db"); //Заменить на переменную
            }
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx);
            //TODO при отсутствии подключения вывести на экран сообщение о необходимости подключения
            //TODO сделать ещё что-то, что не застопорит выполнение процессов. Может перейти в оффлайн-режим.
        }
        return connectNow;
    }
}