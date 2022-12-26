package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.DataBaseEnum;
import lombok.Getter;
import lombok.Setter;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.currentUser;

/**
 * Класс работы с логом открытия/закрытия смен
 * TODO удалять синхронизированные строки для сокращения времени проверки таблицы shiftLog в локальной базе данных.
 * это касается метода syncShiftLog().
 */
@Getter
@Setter
public class ShiftLog {
    int outletId;
    int userId;
    Date date;
    Time time;
    int isClosed;
    /**
     * Метод, записывающий в лог-таблицу дату, время и точку продаж. Он отмечает когда была открыта смена
     * и когда закрыта.
     * @param isClosed - принимает boolean значение в виде ответа на вопрос, закрыто ли?
     */
    public static void shiftLog(boolean isClosed) {
        Connection con = getConnectionLDB();
        if (con != null) {
            Statement statement;
            long nowDate = System.currentTimeMillis();
            Date logDate = new Date(nowDate);
            Time logTime = new Time(nowDate);
            String query = "INSERT INTO shift_log (outlet_id, user_id, date, time, is_closed, loaded) VALUES ("
                    + currentOutlet.getOutletId()
                    + ", " + currentUser.getId()
                    + ", '" + logDate
                    + "', '" + logTime
                    + "', " + decodeIntBoolean(isClosed)
                    + ", 0);";
            try {
                statement = con.createStatement();
                statement.executeUpdate(query);
                statement.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Данный метод реализует синхронизацию таблиц shift_log (сетевой с локальной).<br>
     * В локальной базе данных выгруженные данные помечаются отметкой loaded,<br>
     * после чего в сетевую базу данных эти строки повторно не выгружаются.<br>
     */
    public static void syncShiftLog() {
        LOGGER.log(Level.INFO, "Start syncShiftLog");

        String selectNotLoaded = "SELECT outlet_id, user_id, date, time, is_closed FROM shift_log WHERE loaded = 0 AND outlet_id = " + currentOutlet.getOutletId() + ";";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

        Connection conNetwork = getConnectionNDB();
        Connection conLocal = getConnectionLDB();
        if (conNetwork != null && conLocal != null) {
            Statement statement;
            try {
                statement = conLocal.createStatement();
                ResultSet resultSelectNotLoaded  = statement.executeQuery(selectNotLoaded);

                while(resultSelectNotLoaded.next()) {
                    ShiftLog shiftLog = new ShiftLog();
                    shiftLog.setOutletId(resultSelectNotLoaded.getInt("outlet_id"));
                    shiftLog.setUserId(resultSelectNotLoaded.getInt("user_id"));
                    try {
                        shiftLog.setDate(Date.valueOf(dateFormatter.format(dateFormatter.parse(resultSelectNotLoaded.getString("date")))));
                        shiftLog.setTime(Time.valueOf(timeFormatter.format(timeFormatter.parse(resultSelectNotLoaded.getString("time")))));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    shiftLog.setIsClosed(resultSelectNotLoaded.getInt("is_closed"));

                    insertToSql(DataBaseEnum.NETWORK_DB, "shift_log", "outlet_id, "
                            + "user_id, "
                            + "date, "
                            + "time, "
                            + "is_closed) VALUES ("
                            + shiftLog.getOutletId() + ", "
                            + shiftLog.getUserId() + ", '"
                            + shiftLog.getDate() + "', '"
                            + shiftLog.getTime() + "', "
                            + shiftLog.getIsClosed());
                    resultSelectNotLoaded.close();
                    conNetwork.close();

                    String update = "UPDATE shift_log SET loaded = ? WHERE outlet_id = ?";
                    PreparedStatement prepareStatement = conLocal.prepareStatement(update);
                    prepareStatement.setInt(1, 1);
                    prepareStatement.setInt(2, currentOutlet.getOutletId());
                    prepareStatement.executeUpdate();
                    prepareStatement.close();
                    conLocal.close();
                    LOGGER.log(Level.FINE,"syncShiftLog completed");
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING,"Error connecting to database while sync ShiftLog");
            }
        } else {
            LOGGER.log(Level.WARNING,"Error connecting to database while sync ShiftLog");
        }
    }
}