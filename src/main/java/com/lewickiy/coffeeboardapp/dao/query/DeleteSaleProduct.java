package com.lewickiy.coffeeboardapp.dao.query;

import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;

public class DeleteSaleProduct {
    public static void deleteSaleProduct(SaleProduct deleteProduct) {
        Connection conLocal = getConnectionLDB();
        PreparedStatement prepareStatement;
        String update = "UPDATE sale_product SET corrected = ? WHERE sale_id = "
                + deleteProduct.getId()
                + " AND " + "product_id = "
                + deleteProduct.getProductId();
        try {
            if (conLocal != null) {
                prepareStatement = conLocal.prepareStatement(update);
                prepareStatement.setInt(1, 1);
                prepareStatement.executeUpdate();
                prepareStatement.close();
                conLocal.close();
            }
        } catch (SQLException e) {
            //TODO
            throw new RuntimeException(e);
        }
    }
}