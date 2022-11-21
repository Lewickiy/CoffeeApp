package com.lewickiy.coffeeboardapp.database.query;

import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;

import java.sql.*;
import java.text.ParseException;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.Query.insertToSql;

public class SyncProductSales {
    public static void syncSalesProduct() throws SQLException, ParseException {
        boolean start = true;
        while(start) {
            Connection conNetwork;
            Connection conLocal;
            try {
                conNetwork = getConnection("network_database");
            } catch (SQLException sqlEx) {
                break;
            }

            conLocal = getConnection("local_database");

            if (conNetwork != null) {
                String selectNotLoaded = "SELECT sale_id, product_id, discount_id, price, amount, sum, loaded FROM sale_product WHERE loaded = 0;";
                Statement statement = conLocal.createStatement();
                ResultSet resultSelectNotLoaded  = statement.executeQuery(selectNotLoaded);

                System.out.println("Load sale_product from local database");

                while(resultSelectNotLoaded.next()) {
                    System.out.println("new sale_product loaded...");
                    SaleProduct tempSaleProduct = new SaleProduct();
                    tempSaleProduct.setSaleId(resultSelectNotLoaded.getInt("sale_id"));
                    tempSaleProduct.setProductId(resultSelectNotLoaded.getInt("product_id"));
                    tempSaleProduct.setDiscountId(resultSelectNotLoaded.getInt("discount_id"));
                    tempSaleProduct.setPrice(resultSelectNotLoaded.getDouble("price"));
                    tempSaleProduct.setAmount(resultSelectNotLoaded.getInt("amount"));
                    tempSaleProduct.setSum(resultSelectNotLoaded.getDouble("sum"));

                    int intLoaded = resultSelectNotLoaded.getInt("loaded");

                    if (intLoaded == 1) {
                        tempSaleProduct.setLoaded(true);
                    } else {
                        tempSaleProduct.setLoaded(false);
                    }

                    insertToSql(conNetwork, "network_database", "sale_product", "sale_id, "
                            + "product_id, "
                            + "discount_id, "
                            + "price, "
                            + "amount, "
                            + "sum) VALUES ('"
                            + tempSaleProduct.getSaleId() + "', '"
                            + tempSaleProduct.getProductId() + "', '"
                            + tempSaleProduct.getDiscountId() + "', '"
                            + tempSaleProduct.getPrice() + "', '"
                            + tempSaleProduct.getAmount() + "', '"
                            + tempSaleProduct.getSum() + "'");

                }

                resultSelectNotLoaded.close();
                conNetwork.close();

                String update = "UPDATE sale_product SET loaded = ? WHERE loaded = ?";
                PreparedStatement prepareStatement = conLocal.prepareStatement(update);
                prepareStatement.setInt(1, 1);
                prepareStatement.setInt(2, 0);
                prepareStatement.executeUpdate();
                prepareStatement.close();
            }
            conLocal.close();
            break;
        }
    }
}