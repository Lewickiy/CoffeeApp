package com.lewickiy.coffeeboardapp.database.query;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.Query.insertToSql;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.user.UserList.currentUser;

/**
 * Класс работы с логом открытия/закрытия смен
 * TODO удалять синхронизированные строки для сокращения времени проверки таблицы shiftLog в локальной базе данных.
 * это касается метода syncShiftLog().
 */
public class ShiftLog {
    int outletId;
    int userId;
    Date date;
    Time time;
    int isClosed;

    public int getOutletId() {
        return outletId;
    }

    public void setOutletId(int outletId) {
        this.outletId = outletId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(int isClosed) {
        this.isClosed = isClosed;
    }

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
        Time logTime = new Time(nowDate);
        String query = "INSERT INTO shift_log (outlet_id, user_id, date, time, is_closed, loaded) VALUES ("
                + currentOutlet.getOutletId()
                + ", " + currentUser.getUserId()
                + ", '" + logDate
                + "', '" + logTime
                + "', " + isClosedB
                + ", 0);";

        Statement statement = con.createStatement();
        statement.executeUpdate(query);

        statement.close();
        con.close();
    }

    /**
     * Данный метод реализует синхронизацию таблиц shift_log (сетевой с локальной).<br>
     * В локальной базе данных выгруженные данные помечаются отметкой loaded,<br>
     * после чего в сетевую базу данных эти строки повторно не выгружаются.<br>
     * @throws SQLException - прерывает выполнение метода, не продолжая какие-либо действия break;<br>
     * @throws ParseException - не обрабатывается.
     */
    public static void syncShiftLog() throws SQLException, ParseException {
            Connection conNetwork;
            Connection conLocal = getConnection("local_database");
            try {
                conNetwork = getConnection("network_database");
                String selectNotLoaded = "SELECT outlet_id, user_id, date, time, is_closed FROM shift_log WHERE loaded = 0 AND outlet_id = " + currentOutlet.getOutletId() + ";";
                Statement statement = conLocal.createStatement();
                ResultSet resultSelectNotLoaded  = statement.executeQuery(selectNotLoaded);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

                while(resultSelectNotLoaded.next()) {
                    ShiftLog shiftLog = new ShiftLog();
                    shiftLog.setOutletId(resultSelectNotLoaded.getInt("outlet_id"));
                    shiftLog.setUserId(resultSelectNotLoaded.getInt("user_id"));
                    shiftLog.setDate(Date.valueOf(dateFormatter.format(dateFormatter.parse(resultSelectNotLoaded.getString("date")))));
                    shiftLog.setTime(Time.valueOf(timeFormatter.format(timeFormatter.parse(resultSelectNotLoaded.getString("time")))));
                    shiftLog.setIsClosed(resultSelectNotLoaded.getInt("is_closed"));

                    insertToSql(conNetwork, "network_database", "shift_log", "outlet_id, "
                            + "user_id, "
                            + "date, "
                            + "time, "
                            + "is_closed) VALUES ("
                            + shiftLog.getOutletId() + ", "
                            + shiftLog.getUserId() + ", '"
                            + shiftLog.getDate() + "', '"
                            + shiftLog.getTime() + "', "
                            + shiftLog.getIsClosed());

                }
                resultSelectNotLoaded.close();
                conNetwork.close();

                String update = "UPDATE shift_log SET loaded = ? WHERE outlet_id = ?";
                PreparedStatement prepareStatement = conLocal.prepareStatement(update);
                prepareStatement.setInt(1, 1);
                prepareStatement.setInt(2, currentOutlet.getOutletId());
                prepareStatement.executeUpdate();
                prepareStatement.close();
            } catch (SQLException sqlEx) {
                LOGGER.log(Level.WARNING,"Error connecting to database while sync ShiftLog");
            }
            conLocal.close();
    }
}