package com.lewickiy.coffeeboardapp.dao.info;

import com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.dao.connector.Database;
import com.lewickiy.coffeeboardapp.entities.info.Info;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTable.enterToWorkTable;
import static com.lewickiy.coffeeboardapp.controllers.seller.SellerController.startSync;
import static com.lewickiy.coffeeboardapp.entities.info.Info.info;
import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class syncInfo {
    public static void getInfoMessage() {
        try {
            LOGGER.log(Level.INFO,"Start refresh info messages");
            Connection con = getConnection(Database.NETWORK_DB);
            Statement statement = con.createStatement();
            String query = "SELECT info_for_pos_id, outlet_id, message, delivered, date FROM info_for_pos WHERE info_for_pos_id = (SELECT MIN(info_for_pos_id) FROM info_for_pos WHERE delivered = 0 AND outlet_id = " + currentOutlet.getOutletId() + ")";
            ResultSet resultSet = statement.executeQuery(query);
            info = new Info();
            while (resultSet.next()) {
                info.setInfoId(resultSet.getInt("info_for_pos_id"));
                info.setMessage(resultSet.getString("message"));
                info.setDelivered(resultSet.getBoolean("delivered"));
            }
            resultSet.close();
            statement.close();
            con.close();

            if (info.getInfoId() != 0 && info.getMessage() != null) {
                startSync = false;
                enterToWorkTable(WorkTableChoice.INFO);
            }
            LOGGER.log(Level.FINE,"Refresh info messages completed");
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING,"Failed to connect to network database to download new information messages");
            startSync = true;
        }
    }
}