package com.lewickiy.coffeeboardapp.dao.todaySales;

import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
@Getter
@Setter
public class TodaySales extends SaleProduct {
    private int outletId;
    private int numberOfUnit;
    private String unitOfMeasurement;
    private Date saleDate;
    private Time saleTime;
    private String paymentType;

    public TodaySales(long saleProdId
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
}