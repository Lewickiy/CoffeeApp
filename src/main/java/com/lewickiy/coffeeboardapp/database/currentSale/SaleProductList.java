package com.lewickiy.coffeeboardapp.database.currentSale;

import java.util.ArrayList;

public class SaleProductList {
    public static ArrayList<SaleProduct> currentSaleProducts = new ArrayList<>();
    public static void addSaleProductsList(int saleProdId, int prodSaleId, String prodName, double priceProdSale, int amountProdSale, double sumProdSale) {
        currentSaleProducts.add(new SaleProduct(saleProdId, prodSaleId, prodName, priceProdSale, amountProdSale, sumProdSale));
    }
    public static void addProductToArray(int positionsCount, SaleProduct currentProduct, int saleId) {
        if (positionsCount == 0) {
            addSaleProductsList(currentProduct.getSaleProdId()
                    , currentProduct.getProdSaleId()
                    , currentProduct.getProdName()
                    , currentProduct.getPriceProdSale()
                    , currentProduct.getAmountProdSale()
                    , currentProduct.getSumProdSale());
        } else {
            boolean b = true;
            for (int i = 0; i < currentSaleProducts.size(); i++) {
                if (currentProduct.getProdSaleId() == currentSaleProducts.get(i).getProdSaleId()) {
                    currentSaleProducts.get(i).setAmountProdSale(currentSaleProducts.get(i).getAmountProdSale() + currentProduct.getAmountProdSale());
                    currentSaleProducts.get(i).setSumProdSale(currentSaleProducts.get(i).getPriceProdSale() * currentSaleProducts.get(i).getAmountProdSale());
                    b = false;
                    break;
                }
            }
            if (b == true) {
                addSaleProductsList(currentProduct.getSaleProdId()
                        , currentProduct.getProdSaleId()
                        , currentProduct.getProdName()
                        , currentProduct.getPriceProdSale()
                        , currentProduct.getAmountProdSale()
                        , currentProduct.getSumProdSale());
            }
        }
    }
}
