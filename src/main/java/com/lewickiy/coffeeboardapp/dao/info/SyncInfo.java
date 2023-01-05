package com.lewickiy.coffeeboardapp.dao.info;

import com.lewickiy.coffeeboardapp.entities.info.Info;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;
import static com.lewickiy.coffeeboardapp.entities.info.Info.info;
import static com.lewickiy.coffeeboardapp.entities.info.Info.setNewMessage;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class SyncInfo {

    public static void getInfoMessage() {
        info = new Info();
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
                    System.out.println(info.getInfoId() + " info id, " + info.getMessage() + " message, " + info.getDelivered());
                    setNewMessage(true);
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
        }
    }
}