package com.lewickiy.coffeeboardapp.entities.saleproduct;

import java.util.ArrayList;
import java.util.List;

public class SaleProductList {
    public static List<SaleProduct> currentSaleProducts = new ArrayList<>();
    public static void addSaleProductsList(long saleProdId
            , int prodSaleId
            , String prodName
            , double priceProdSale
            , int discountId
            , int discount
            , int amountProdSale
            , double sumProdSale) {

        currentSaleProducts.add(new SaleProduct(saleProdId
                , prodSaleId
                , prodName
                , priceProdSale
                , discountId
                , discount
                , amountProdSale
                , sumProdSale));
    }

    public static void addProductToArray(int positionsCount, SaleProduct currentProduct) {
        currentProduct.setSum((currentProduct.getPrice()
                - (currentProduct.getPrice() * currentProduct.getDiscount() / 100))
                * currentProduct.getAmount());
        if (positionsCount == 0) {
            addProductIf(currentProduct);
        } else {
            boolean b = true;

            for (SaleProduct currentSaleProduct : currentSaleProducts) {
                if (currentProduct.getProductId() == currentSaleProduct.getProductId()
                        && currentProduct.getDiscountId() == currentSaleProduct.getDiscountId()) {
                    currentSaleProduct.setAmount(
                            currentSaleProduct.getAmount()
                                    + currentProduct.getAmount());
                    currentSaleProduct.setSum((
                            currentSaleProduct.getPrice()
                                    - (currentSaleProduct.getPrice()
                                    * currentSaleProduct.getDiscount() / 100))
                            * currentSaleProduct.getAmount());
                    b = false;
                    break;
                }
            }
            if (b) {
                addProductIf(currentProduct);
            }
        }
    }
    /**
     * Helper method for the addProductToArray() method.<br>
     * Adds the entire Product if this product is the first time it appears in the list.<br>
     * @param currentProduct Takes as parameter the current product from the ArrayList currentSaleProducts.<br>
     */
    static void addProductIf(SaleProduct currentProduct) {
        addSaleProductsList(currentProduct.getId()
                , currentProduct.getProductId()
                , currentProduct.getProduct()
                , currentProduct.getPrice()
                , currentProduct.getDiscountId()
                , currentProduct.getDiscount()
                , currentProduct.getAmount()
                , currentProduct.getSum());
    }
}