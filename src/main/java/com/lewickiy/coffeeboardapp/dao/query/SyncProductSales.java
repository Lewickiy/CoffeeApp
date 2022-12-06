package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.Database;
import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;

import java.sql.*;
import java.text.ParseException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;

public class SyncProductSales {
    public static void syncSalesProduct() throws SQLException, ParseException {
        LOGGER.log(Level.INFO, "Start syncSalesProduct");
        Connection conNetwork = null;
        Connection conLocal;

        try {
            conNetwork = getConnection(Database.NETWORK_DB);
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Connection to network database failed");
        }

        conLocal = getConnection(Database.LOCAL_DB);

        if (conNetwork != null) {
            String selectNotLoaded = "SELECT sale_id, product_id, discount_id, price, amount, sum, loaded, corrected FROM sale_product WHERE loaded = 0;";
            Statement statement = conLocal.createStatement();
            ResultSet resultSelectNotLoaded  = statement.executeQuery(selectNotLoaded);

            while(resultSelectNotLoaded.next()) {
                SaleProduct tempSaleProduct = new SaleProduct();
                tempSaleProduct.setSaleId(resultSelectNotLoaded.getLong("sale_id"));
                tempSaleProduct.setProductId(resultSelectNotLoaded.getInt("product_id"));
                tempSaleProduct.setDiscountId(resultSelectNotLoaded.getInt("discount_id"));
                tempSaleProduct.setPrice(resultSelectNotLoaded.getDouble("price"));
                tempSaleProduct.setAmount(resultSelectNotLoaded.getInt("amount"));
                tempSaleProduct.setSum(resultSelectNotLoaded.getDouble("sum"));
                tempSaleProduct.setCorrected(decodeIntBoolean(resultSelectNotLoaded.getInt("corrected")));
                tempSaleProduct.setLoaded(decodeIntBoolean(resultSelectNotLoaded.getInt("loaded")));

                insertToSql(conNetwork, "network_database", "sale_product", "sale_id, "
                        + "price_id, "
                        + "discount_id, "
                        + "price, "
                        + "amount, "
                        + "sum, "
                        + "corrected) VALUES ('"
                        + tempSaleProduct.getSaleId() + "', '"
                        + tempSaleProduct.getProductId() + "', '"
                        + tempSaleProduct.getDiscountId() + "', '"
                        + tempSaleProduct.getPrice() + "', '"
                        + tempSaleProduct.getAmount() + "', '"
                        + tempSaleProduct.getSum() + "', '"
                        + decodeIntBoolean(tempSaleProduct.isCorrected()) + "'");
            }
            resultSelectNotLoaded.close();
            conNetwork.close();

            String update = "UPDATE sale_product SET loaded = ? WHERE loaded = ?";
            PreparedStatement prepareStatement = conLocal.prepareStatement(update);
            prepareStatement.setInt(1, 1);
            prepareStatement.setInt(2, 0);
            prepareStatement.executeUpdate();
            prepareStatement.close();
            conLocal.close();
            LOGGER.log(Level.FINE,"Sync Sale products is ok...");
        }
    }
}