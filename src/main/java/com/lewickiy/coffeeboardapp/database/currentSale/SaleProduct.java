package com.lewickiy.coffeeboardapp.database.currentSale;

public class SaleProduct {
    private int saleProdId;
    private int prodSaleId;
    private String prodName;
    private double priceProdSale;
    private int amountProdSale;
    private double sumProdSale;

    public SaleProduct(int saleProdId, int prodSaleId, String prodName, double priceProdSale, int amountProdSale, double sumProdSale) {
        this.saleProdId = saleProdId;
        this.prodSaleId = prodSaleId;
        this.prodName = prodName;
        this.priceProdSale = priceProdSale;
        this.amountProdSale = amountProdSale;
        this.sumProdSale = sumProdSale;
    }

    public SaleProduct(int prodSaleId, String prodName, double priceProdSale) {
        this.prodSaleId = prodSaleId;
        this.prodName = prodName;
        this.priceProdSale = priceProdSale;
    }

    public int getSaleProdId() {
        return saleProdId;
    }

    public void setSaleProdId(int saleProdId) {
        this.saleProdId = saleProdId;
    }

    public int getProdSaleId() {
        return prodSaleId;
    }

    public void setProdSaleId(int prodSaleId) {
        this.prodSaleId = prodSaleId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public double getPriceProdSale() {
        return priceProdSale;
    }

    public void setPriceProdSale(double priceProdSale) {
        this.priceProdSale = priceProdSale;
    }

    public int getAmountProdSale() {
        return amountProdSale;
    }

    public void setAmountProdSale(int amountProdSale) {
        this.amountProdSale = amountProdSale;
    }

    public double getSumProdSale() {
        return sumProdSale;
    }

    public void setSumProdSale(double sumProdSale) {
        this.sumProdSale = sumProdSale;
    }
}
