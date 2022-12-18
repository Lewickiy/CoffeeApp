package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.Database;
import com.lewickiy.coffeeboardapp.entities.currentsale.CurrentSale;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;

public class SyncSales {
    public static void syncSales() throws SQLException, ParseException {
        LOGGER.log(Level.INFO, "Start syncSales");
        Connection conNetwork;
        Connection conLocal = getConnection(Database.LOCAL_DB);
        try {
            conNetwork = getConnection(Database.NETWORK_DB);
            if (conNetwork != null) {
                String selectNotLoaded = "SELECT sale_id, user_id, outlet_id, date, time, paymenttype_id, client_id, loaded FROM sale WHERE loaded = 0 AND outlet_id = " + currentOutlet.getOutletId() + ";";
                Statement statement = conLocal.createStatement();
                ResultSet resultSelectNotLoaded = statement.executeQuery(selectNotLoaded);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

                while (resultSelectNotLoaded.next()) {
                    CurrentSale currentSale1 = new CurrentSale(resultSelectNotLoaded.getLong("sale_id")
                            , resultSelectNotLoaded.getInt("user_id")
                            , resultSelectNotLoaded.getInt("outlet_id"));
                    currentSale1.setCurrentDate((Date.valueOf(dateFormatter.format(dateFormatter.parse(resultSelectNotLoaded.getString("date"))))));
                    currentSale1.setCurrentTime(Time.valueOf(timeFormatter.format(timeFormatter.parse(resultSelectNotLoaded.getString("time")))));
                    currentSale1.setPaymentTypeId(resultSelectNotLoaded.getInt("paymenttype_id"));
                    currentSale1.setClientId(resultSelectNotLoaded.getInt("client_id"));
                    currentSale1.setLoaded(decodeIntBoolean(resultSelectNotLoaded.getInt("loaded")));

                    insertToSql(conNetwork, "network_database", "sale", "sale_id, "
                            + "user_id, "
                            + "outlet_id, "
                            + "date, "
                            + "time, "
                            + "paymenttype_id, "
                            + "client_id) VALUES ('"
                            + currentSale1.getSaleId() + "', '"
                            + currentSale1.getUserId() + "', '"
                            + currentSale1.getOutletId() + "', '"
                            + currentSale1.getCurrentDate() + "', '"
                            + currentSale1.getCurrentTime() + "', '"
                            + currentSale1.getPaymentTypeId() + "', '"
                            + currentSale1.getClientId() + "'");
                }

                resultSelectNotLoaded.close();
                conNetwork.close();

                String update = "UPDATE sale SET loaded = ? WHERE outlet_id = ?";
                PreparedStatement prepareStatement = conLocal.prepareStatement(update);
                prepareStatement.setInt(1, 1);
                prepareStatement.setInt(2, currentOutlet.getOutletId());
                prepareStatement.executeUpdate();
                prepareStatement.close();
                conLocal.close();
            }
            LOGGER.log(Level.FINE,"syncSales completed");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING, "Error connecting to database while sync Sales");
        }
    }
}