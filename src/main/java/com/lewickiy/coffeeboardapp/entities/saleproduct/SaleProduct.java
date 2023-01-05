package com.lewickiy.coffeeboardapp.entities.saleproduct;

import com.lewickiy.coffeeboardapp.dao.connector.DataBaseEnum;
import com.lewickiy.coffeeboardapp.entities.currentsale.CurrentSale;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.lewickiy.coffeeboardapp.dao.query.Query.insertToSql;
@Getter
@Setter
@NoArgsConstructor
public class SaleProduct {
    private long id;
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

    public SaleProduct(long saleProdId
            , int prodSaleId
            , String prodName
            , double priceProdSale
            , int discountId
            , int discount
            , int amountProdSale
            , double sumProdSale) {
        this.id = saleProdId;
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

    public static void addSaleProductsToLocalDB(List<SaleProduct> currentSaleProducts, CurrentSale currentSale) {
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            insertToSql(DataBaseEnum.LOCAL_DB, "sale_product", "sale_id, "
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
    }
}