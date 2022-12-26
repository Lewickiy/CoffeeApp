package com.lewickiy.coffeeboardapp.dao.todaySales;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesList.todaySalesArrayList;

public class TodaySalesListReload {
    public static void todaySalesListReload() {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

        Connection con;
        Statement statement;
        ResultSet rs;

        String query = "SELECT sale.sale_id" +
                ", sale.time" +
                ", product.product_id" +
                ", product.product" +
                ", product.number_of_unit" +
                ", product.unit_of_measurement" +
                ", sale_product.price" +
                ", sale_product.amount" +
                ", discount.discount_id" +
                ", discount.discount" +
                ", sale_product.sum" +
                ", sale_product.corrected" +
                ", paymenttype.paymenttype" +
                " FROM sale " +
                "FULL OUTER JOIN sale_product ON sale.sale_id = sale_product.sale_id " +
                "FULL OUTER JOIN paymenttype ON sale.paymenttype_id = paymenttype.paymenttype_id " +
                "FULL OUTER JOIN discount ON sale_product.discount_id = discount.discount_id " +
                "FULL OUTER JOIN product ON sale_product.product_id = product.product_id" + " WHERE corrected = 0 ";

        try {
            con = getConnectionLDB();
            if (con != null) {
                statement = con.createStatement();
                rs = statement.executeQuery(query);
                while(rs.next()) {
                    if (rs.getString(1) == null) {
                        break;
                    } else {
                        TodaySales tempSale = new TodaySales(rs.getLong("sale_id")
                                , rs.getInt("product_id")
                                , rs.getString("product") //product
                                , rs.getDouble("price") //price
                                , rs.getInt(9) //discount id
                                , rs.getInt(10) //discount
                                , rs.getInt(8) //amount
                                , rs.getDouble(11)); //sum
                        tempSale.setSaleTime(Time.valueOf(timeFormatter.format(timeFormatter.parse(rs.getString(2)))));
                        tempSale.setNumberOfUnit(rs.getInt(5));
                        tempSale.setUnitOfMeasurement(rs.getString(6));
                        tempSale.setPaymentType(rs.getString("paymenttype"));
                        todaySalesArrayList.add(tempSale);
                    }
                }
                rs.close();
                statement.close();
                con.close();
            } else {
                LOGGER.log(Level.WARNING, "Connecting to LocalDatabase for deleting SaleProduct failed");
            }
        } catch (SQLException | ParseException sqlEx) {
            LOGGER.log(Level.WARNING, "Deleting SaleProduct error");
        }
    }
}