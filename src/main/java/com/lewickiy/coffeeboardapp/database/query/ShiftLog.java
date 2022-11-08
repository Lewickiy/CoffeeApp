package com.lewickiy.coffeeboardapp.database.query;

import java.sql.*;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.user.UserList.currentUser;

/**
 * Класс работы с логом открытия/закрытия смен
 */
public class ShiftLog {
    /**
     * Метод, записывающий в лог-таблицу дату, время и точку продаж. Он отмечает когда была открыта смена
     * и когда закрыта.
     * @param isClosed - принимает boolean значение в виде ответа на вопрос, закрыто ли?
     * @throws SQLException - пока не обрабатывается.
     */
    public static void shiftLog(boolean isClosed) throws SQLException {
        int isClosedB = 0;
        if (isClosed) {
            isClosedB = 1;
        }
        Connection con = getConnection("local_database");
        long nowDate = System.currentTimeMillis();
        Date logDate = new Date(nowDate);
        long nowTime = System.currentTimeMillis();
        Time logTime = new Time(nowTime);
        String query = "INSERT INTO shift_log (outlet_id, user_id, date, time, is_closed, loaded) VALUES ("
                + currentOutlet.getOutletId()
                + ", " + currentUser.getUserId()
                + ", '" + logDate
                + "', '"
                + logTime
                + "',"
                + isClosedB
                +", 0);";

        Statement statement = con.createStatement();
        statement.executeUpdate(query);

        statement.close();
        con.close();
    }
    public static void syncShiftLog() {
        //TODO метод синхронизации с сетевой базой данных
    }
}