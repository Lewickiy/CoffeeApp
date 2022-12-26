package com.lewickiy.coffeeboardapp.dao.info;

import com.lewickiy.coffeeboardapp.controllers.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.entities.info.Info;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.actions.worktable.WorkTable.enterToWorkTable;
import static com.lewickiy.coffeeboardapp.controllers.seller.SellerController.startSync;
import static com.lewickiy.coffeeboardapp.entities.info.Info.info;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class SyncInfo {
    public static void getInfoMessage() {
        info = new Info();
        LOGGER.log(Level.INFO,"Start refresh info messages");
        Connection con = getConnectionNDB();
        String query = "SELECT info_for_pos_id, outlet_id, message, delivered, date FROM info_for_pos WHERE info_for_pos_id = (SELECT MIN(info_for_pos_id) FROM info_for_pos WHERE delivered = 0 AND outlet_id = " + currentOutlet.getOutletId() + ")";
        Statement statement;
        ResultSet resultSet;

        if (con != null) {
            try {
                statement = con.createStatement();
                resultSet = statement.executeQuery(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    if (!resultSet.next()) break;
                    info.setInfoId(resultSet.getInt("info_for_pos_id"));
                    info.setMessage(resultSet.getString("message"));
                    info.setDelivered(resultSet.getBoolean("delivered"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                resultSet.close();
                statement.close();
                con.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (info.getInfoId() != 0 && info.getMessage() != null) {
                startSync = false;
                enterToWorkTable(WorkTableChoice.INFO);
            }
            LOGGER.log(Level.FINE,"Refresh info messages completed");
        } else {
            LOGGER.log(Level.WARNING,"Failed to connect to network database to download new information messages");
            startSync = true;
        }
    }
}