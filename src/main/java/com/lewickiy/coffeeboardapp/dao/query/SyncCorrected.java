package com.lewickiy.coffeeboardapp.dao.query;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;

public class SyncCorrected {

    public static void syncCorrectedSales() {
        ArrayList<Long> saleId = new ArrayList<>();
        ArrayList<Integer> priceId = new ArrayList<>();

        String selectCorrected = "SELECT sale_id, product_id FROM sale_product WHERE corrected = 1;";

        Connection conNDB;
        Connection conLDB;
        Statement statement;
        ResultSet resultSelectNotLoaded;
        PreparedStatement prepareStatement;

        try {
            conNDB = getConnectionNDB();
            conLDB = getConnectionLDB();
            if (conNDB != null && conLDB != null) {
                statement = conLDB.createStatement();
                resultSelectNotLoaded = statement.executeQuery(selectCorrected);

                while (resultSelectNotLoaded.next()) {
                    saleId.add(resultSelectNotLoaded.getLong("sale_id"));
                    priceId.add(resultSelectNotLoaded.getInt("product_id"));
                }
                if (saleId.size() != 0) {

                    for (int i = 0; i < saleId.size(); i++) {
                        String query = "UPDATE sale_product SET corrected = '" + 1 + "' WHERE sale_id = '" + saleId.get(i) + "' AND price_id = '" + priceId.get(i) + "'";
                        prepareStatement = conNDB.prepareStatement(query);
                        prepareStatement.executeUpdate();
                        prepareStatement.close();
                    }
                    conNDB.close();
                    saleId.clear();
                    priceId.clear();
                }
                LOGGER.log(Level.FINE, "syncCorrectedSales completed");
            } else {
                LOGGER.log(Level.WARNING, "Error connecting to database while syncCorrectedSales");
                conLDB.close();
            }
        } catch (SQLException sqlEx) {
        LOGGER.log(Level.WARNING, "Statement, PrepareStatement or ResultSet Error while syncCorrectedSales");
        }
    }
}