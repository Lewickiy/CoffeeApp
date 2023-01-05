package com.lewickiy.coffeeboardapp.entities.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class UserList {
    public static List<User> users = new ArrayList<>();
    /**
     * Здесь есть объект "Текущий пользователь", с которым дальше работает программа.
     * Данные о нём заполняются при входе в систему, после верификации.
     * На момент запуска этот пользователь не имеет никаких данных (см. ниже),
     * но после прохождения верификации, данные о нём заполняются из ArrayList users.
     * В последующем я буду работать с этими данными в системе при создании продаж,
     * SellerController
     * подсчёта выручки и прочего.
     */
    public static User currentUser = new User();

    public static void createUsersList() {
        Connection con;
        ResultSet resultSet;
        try {
            con = getConnectionLDB();
            if (con != null) {
                resultSet = selectAllFromSql(con, "local_database","user");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                while(resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String login = resultSet.getString("login");
                    String password = resultSet.getString("password");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String patronymic = resultSet.getString("patronymic");
                    Date birthday;
                    try {
                        birthday = formatter.parse(resultSet.getString("birthday"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                        //TODO catch
                    }
                    String phone = resultSet.getString("phone");
                    boolean activeStuff = decodeIntBoolean(resultSet.getInt("active_stuff"));
                    boolean administrator = decodeIntBoolean(resultSet.getInt("administrator"));
                    users.add(new User(userId, login, password, firstName, lastName, patronymic, birthday, phone, administrator, activeStuff));
                }
                resultSet.close();
                con.close();
            } else {
                //TODO log
            }
        } catch (SQLException sqlEx) {
            //TODO log
        }
    }
}