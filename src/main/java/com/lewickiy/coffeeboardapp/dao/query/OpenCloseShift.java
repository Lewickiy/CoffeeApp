package com.lewickiy.coffeeboardapp.dao.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class OpenCloseShift {
    public static void updateShiftSql(boolean sql, double cashDeposit) {
        Connection con = getConnectionLDB();
        if (con != null) {
            Statement statement;
            String query = "UPDATE shift SET is_closed = "
                    + decodeIntBoolean(sql) + ", cash_deposit = " + cashDeposit
                    + " WHERE outlet_id = "
                    + currentOutlet.getOutletId() + ";";
            try {
                statement = con.createStatement();
                statement.executeUpdate(query);
                statement.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOGGER.log(Level.WARNING,"Failed to connect to network database for update ShiftLog local database");
        }
    }
}