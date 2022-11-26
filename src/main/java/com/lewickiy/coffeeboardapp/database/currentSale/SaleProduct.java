package com.lewickiy.coffeeboardapp.database.currentSale;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.Query.insertToSql;

public class SaleProduct {

    private int saleId;
    private int productId;
    private String product;
    private int numberOfUnit;
    private String unitOfMeasurement;
    private double price;
    private int discountId;
    private int discount;
    private int amount;
    private double sum;
    private boolean corrected;
    private boolean loaded;

    public SaleProduct() {
    }

    public SaleProduct(int saleProdId
            , int prodSaleId
            , String prodName
            , double priceProdSale
            , int discountId
            , int discount
            , int amountProdSale
            , double sumProdSale) {
        this.saleId = saleProdId;
        this.productId = prodSaleId;
        this.product = prodName;
        this.price = priceProdSale;
        this.discountId = discountId;
        this.discount = discount;
        this.amount = amountProdSale;
        this.sum = sumProdSale;
    }

    public SaleProduct(int prodSaleId, String prodName, double priceProdSale) {
        this.productId = prodSaleId;
        this.product = prodName;
        this.price = priceProdSale;
    }

    public boolean isCorrected() {
        return corrected;
    }

    public void setCorrected(boolean corrected) {
        this.corrected = corrected;
    }

    public int getNumberOfUnit() {
        return numberOfUnit;
    }

    public void setNumberOfUnit(int numberOfUnit) {
        this.numberOfUnit = numberOfUnit;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public static void addSaleProductsToLocalDB(ArrayList<SaleProduct> currentSaleProducts, CurrentSale currentSale) throws SQLException {
        Connection con = getConnection("local_database");
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            insertToSql(con, "local_database", "sale_product", "sale_id, "
                    + "product_id, "
                    + "discount_id, "
                    + "price, "
                    + "amount, "
                    + "sum, "
                    + "loaded) VALUES ('"
                    + currentSale.getSaleId() + "', '"
                    + currentSaleProduct.getProductId() + "', '"
                    + currentSaleProduct.getDiscountId() + "', '"
                    + currentSaleProduct.getPrice() + "', '"
                    + currentSaleProduct.getAmount() + "', '"
                    + currentSaleProduct.getSum() + "', '"
                    + 0 + "'");

        }
        con.close();
    }
}