package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class OpenCloseShift {
    public static void updateShiftSql(boolean sql, double cashDeposit) throws SQLException {
        Connection con = getConnection(Database.LOCAL_DB);
        int sqlInt = 0;

        if (sql) {
            sqlInt = 1;
        }
        String query = "UPDATE shift SET is_closed = "
                + sqlInt + ", cash_deposit = " + cashDeposit
                + " WHERE outlet_id = "
                + currentOutlet.getOutletId() + ";";

        Statement statement = con.createStatement();
        statement.executeUpdate(query);

        statement.close();
        con.close();
    }
}