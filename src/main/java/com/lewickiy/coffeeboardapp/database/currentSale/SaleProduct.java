package com.lewickiy.coffeeboardapp.database.currentSale;

import com.lewickiy.coffeeboardapp.database.DatabaseConnector;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SaleProduct {
    private int saleId;
    private int productId;
    private String product;
    private double price;
    private int discountId;
    private int amount;
    private double sum;

    public SaleProduct(int saleProdId, int prodSaleId, String prodName, double priceProdSale, int amountProdSale, double sumProdSale) {
        this.saleId = saleProdId;
        this.productId = prodSaleId;
        this.product = prodName;
        this.price = priceProdSale;
        this.amount = amountProdSale;
        this.sum = sumProdSale;
    }

    public SaleProduct(int prodSaleId, String prodName, double priceProdSale) {
        this.productId = prodSaleId;
        this.product = prodName;
        this.price = priceProdSale;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getSum() {
        return sum;
    }

    public void setSumProdSale(double sumProdSale) {
        this.sumProdSale = sumProdSale;
    }
}