package com.lewickiy.coffeeboardapp.database.local.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;

import java.sql.Date;
import java.sql.Time;

public class TodaySales extends SaleProduct {
    private int outletId;
    private int numberOfUnit;
    private String unitOfMeasurement;
    private Date saleDate; //Помимо полей Продукта имеет Дату продажи
    private Time saleTime; //И время продажи
    private String paymentType; //А также тип оплаты типа String

    public TodaySales(int saleProdId
            , int prodSaleId
            , String prodName
            , double priceProdSale
            , int discountId
            , int discount
            , int amountProdSale
            , double sumProdSale) {

        super(saleProdId
                , prodSaleId
                , prodName
                , priceProdSale
                , discountId
                , discount
                , amountProdSale
                , sumProdSale);
    }

    public int getOutletId() {
        return outletId;
    }

    public void setOutletId(int outletId) {
        this.outletId = outletId;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public Time getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Time saleTime) {
        this.saleTime = saleTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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
}