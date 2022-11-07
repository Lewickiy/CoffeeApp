package com.lewickiy.coffeeboardapp.database.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;

public class OpenCloseShift {
    public static void updateShiftSql(boolean sql, double cashDeposit) throws SQLException {
        Connection con = getConnection("local_database");
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