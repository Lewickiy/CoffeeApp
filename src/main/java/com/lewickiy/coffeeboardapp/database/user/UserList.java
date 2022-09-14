package com.lewickiy.coffeeboardapp.database.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;

public class UserList {
    public static ArrayList<User> users = new ArrayList<>();
    /**
     * Здесь у меня есть объект "Текущий пользователь", с которым дальше работает программа.
     * Данные о нём заполняются при входе в систему, после верификации.
     * На момент запуска этот пользователь не имеет никаких данных (см. ниже),
     * но после прохождения верификации, данные о нём заполняются из ArrayList users.
     * В последующем я буду работать с этими данными в системе при создании продаж,
     * SellerController
     * подсчёта выручки и прочее.
     */
    public static User currentUser = new User(0
            , "unknown"
            , "nopassword"
            , "noname"
            , "nosurname"
            , "nophone"
            ,  false
            , false);

    public static void createUsersList() throws SQLException {
        Statement statement = getConnection().createStatement(); //создаётся подключение
        String query = "SELECT * FROM user"; //создаётся запрос к базе данных
        ResultSet resultSet = statement.executeQuery(query);

        while(resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String login = resultSet.getString("login");
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String patronymic = resultSet.getString("patronymic");
            Date birthday = resultSet.getDate("birthday");
            String phone = resultSet.getString("phone");
            boolean administrator = resultSet.getBoolean("administrator");
            boolean activeStuff = resultSet.getBoolean("active_stuff");
            users.add(new User(userId, login, password, firstName, lastName, patronymic, birthday, phone, administrator, activeStuff));
        }
    }
}
