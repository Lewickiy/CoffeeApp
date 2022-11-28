package com.lewickiy.coffeeboardapp.database.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.database.connection.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.user.UserList.currentUser;

/**
 * Класс, отвечающий за подсчёт заработка текущего Пользователя за сегодняшний день.
 */
public class UserEarnings {
    /**
     * Данный метод непосредственно занимается подсчётом заработной платы за смену текущего бариста (сегодня).<br>
     * Этот метод должен вызываться при закрытии смены. Он у меня очень тяжёлый.<br>
     * Необходимо пересмотреть запросы для ускорения. Пока всё очень тяжело.<br>
     * @return - Метод возвращает сумму заработной платы за текущую смену типом double<br>
     * @throws SQLException - ...
     */
    public static double reloadUserEarnings() throws SQLException {
        double userEarnings = 0;
        long nowDate = System.currentTimeMillis(); //Дата сейчас.
        java.sql.Date currentDate = new java.sql.Date(nowDate);
        System.out.println(currentDate);
        Statement statement = getConnection("network_database").createStatement(); //создаётся подключение
        String query = "SELECT * FROM `sale` WHERE `user_id` = " + currentUser.getUserId() +" AND `date` = '" + currentDate + "'"; //создаётся запрос к базе данных
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            int saleId = resultSet.getInt("sale_id");
            Statement statement1 = getConnection("network_database").createStatement();
            String sumQuery = "SELECT * FROM `sale_product` WHERE `sale_id` = " + saleId;
            ResultSet resultSet1 = statement1.executeQuery(sumQuery);
            while (resultSet1.next()) {
                double bufferSum = resultSet1.getDouble("sum");
                userEarnings = userEarnings + bufferSum;
            }
        }
         return (userEarnings * 5.0 / 100.0) + 1000.0;
    }
}