package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.dao.connector.DataBaseEnum;
import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;

import java.sql.*;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;

public class SyncProductSales {
    public static void syncSalesProduct() {
        String selectNotLoaded = "SELECT sale_id, product_id, discount_id, price, amount, sum, loaded, corrected FROM sale_product WHERE loaded = 0;";

        Connection conLocal = getConnectionLDB();
        if (conLocal != null) {
            Statement statement;
            try {
                statement = conLocal.createStatement();
                ResultSet resultSelectNotLoaded  = statement.executeQuery(selectNotLoaded);
                while(resultSelectNotLoaded.next()) {
                    SaleProduct tempSaleProduct = new SaleProduct();
                    tempSaleProduct.setId(resultSelectNotLoaded.getLong("sale_id"));
                    tempSaleProduct.setProductId(resultSelectNotLoaded.getInt("product_id"));
                    tempSaleProduct.setDiscountId(resultSelectNotLoaded.getInt("discount_id"));
                    tempSaleProduct.setPrice(resultSelectNotLoaded.getDouble("price"));
                    tempSaleProduct.setAmount(resultSelectNotLoaded.getInt("amount"));
                    tempSaleProduct.setSum(resultSelectNotLoaded.getDouble("sum"));
                    tempSaleProduct.setCorrected(decodeIntBoolean(resultSelectNotLoaded.getInt("corrected")));
                    tempSaleProduct.setLoaded(decodeIntBoolean(resultSelectNotLoaded.getInt("loaded")));

                    insertToSql(DataBaseEnum.NETWORK_DB, "sale_product", "sale_id, "
                            + "price_id, "
                            + "discount_id, "
                            + "price, "
                            + "amount, "
                            + "sum, "
                            + "corrected) VALUES ('"
                            + tempSaleProduct.getId() + "', '"
                            + tempSaleProduct.getProductId() + "', '"
                            + tempSaleProduct.getDiscountId() + "', '"
                            + tempSaleProduct.getPrice() + "', '"
                            + tempSaleProduct.getAmount() + "', '"
                            + tempSaleProduct.getSum() + "', '"
                            + decodeIntBoolean(tempSaleProduct.isCorrected()) + "'");
                }
                resultSelectNotLoaded.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String update = "UPDATE sale_product SET loaded = ? WHERE loaded = ?";
            PreparedStatement prepareStatement;
            try {
                prepareStatement = conLocal.prepareStatement(update);
                prepareStatement.setInt(1, 1);
                prepareStatement.setInt(2, 0);
                prepareStatement.executeUpdate();
                prepareStatement.close();
                conLocal.close();
                LOGGER.log(Level.FINE,"syncSalesProduct completed");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING,"Statement, PrepareStatement or ResultSet Error while syncSalesProduct");
            }
        } else {
            LOGGER.log(Level.WARNING,"Error connecting to database while syncSalesProduct");
        }
    }
}