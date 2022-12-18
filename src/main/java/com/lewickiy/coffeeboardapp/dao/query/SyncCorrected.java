package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;

public class SyncCorrected {

    public static void syncCorrectedSales() {
        ArrayList<Long> saleId = new ArrayList<>();
        ArrayList<Integer> priceId = new ArrayList<>();
        LOGGER.log(Level.INFO, "Start syncCorrectedSales");
        try {
            Connection conLDB = getConnection(Database.LOCAL_DB);
            String selectCorrected = "SELECT sale_id, product_id FROM sale_product WHERE corrected = 1;";
            Statement statement = conLDB.createStatement();
            ResultSet resultSelectNotLoaded = statement.executeQuery(selectCorrected);
            while (resultSelectNotLoaded.next()) {
                saleId.add(resultSelectNotLoaded.getLong("sale_id"));
                priceId.add(resultSelectNotLoaded.getInt("product_id"));
            }
            if (saleId.size() != 0) {
                Connection conNDB = getConnection(Database.NETWORK_DB);
                for (int i = 0; i < saleId.size(); i++) {
                    String query = "UPDATE sale_product SET corrected = '" + 1 + "' WHERE sale_id = '" + saleId.get(i) + "' AND price_id = '" + priceId.get(i) + "'";
                    PreparedStatement prepareStatement = conNDB.prepareStatement(query);
                    prepareStatement.executeUpdate();
                    prepareStatement.close();
                }
                conNDB.close();
            }
            LOGGER.log(Level.FINE,"syncCorrectedSales completed");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error connecting to database while syncCorrectedSales");
            throw new RuntimeException(e);
        }
    }
}