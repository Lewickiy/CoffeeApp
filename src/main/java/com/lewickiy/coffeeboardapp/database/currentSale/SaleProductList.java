package com.lewickiy.coffeeboardapp.database.currentSale;

import java.util.ArrayList;

public class SaleProductList {
    public static ArrayList<SaleProduct> currentSaleProducts = new ArrayList<>();
    public static void addSaleProductsList(int saleProdId, int prodSaleId, String prodName, double priceProdSale, int discountId, int discount, int amountProdSale, double sumProdSale) {
        currentSaleProducts.add(new SaleProduct(saleProdId, prodSaleId, prodName, priceProdSale, discountId, discount, amountProdSale, sumProdSale));
    }
    public static void addProductToArray(int positionsCount
            , CurrentSale currentSale
            , SaleProduct currentProduct
            , int saleId) {
        currentProduct.setSum((currentProduct.getPrice()
                - (currentProduct.getPrice() * currentProduct.getDiscountId() / 100))
                * currentProduct.getAmount());
        if (positionsCount == 0) {
            addSaleProductsList(currentSale.getSaleId()
                    , currentProduct.getProductId()
                    , currentProduct.getProduct()
                    , currentProduct.getPrice()
                    , currentProduct.getDiscountId()
                    , currentProduct.getDiscount()
                    , currentProduct.getAmount()
                    , currentProduct.getSum());
        } else {
            boolean b = true;
            for (int i = 0; i < currentSaleProducts.size(); i++) {
                if (currentProduct.getProductId() == currentSaleProducts.get(i).getProductId()
                        && currentProduct.getDiscountId() == currentSaleProducts.get(i).getDiscountId()) {
                    currentSaleProducts.get(i).setAmount(
                            currentSaleProducts.get(i).getAmount()
                                    + currentProduct.getAmount());
                    currentSaleProducts.get(i).setSum((
                            currentSaleProducts.get(i).getPrice()
                                    - (currentSaleProducts.get(i).getPrice()
                                    * currentSaleProducts.get(i).getDiscountId() / 100))
                            * currentSaleProducts.get(i).getAmount());
                    b = false;
                    break;
                }
            }
            if (b == true) {
                addSaleProductsList(currentProduct.getSaleId()
                        , currentProduct.getProductId()
                        , currentProduct.getProduct()
                        , currentProduct.getPrice()
                        , currentProduct.getDiscountId()
                        , currentProduct.getDiscount()
                        , currentProduct.getAmount()
                        , currentProduct.getSum());
            }
        }
    }
}