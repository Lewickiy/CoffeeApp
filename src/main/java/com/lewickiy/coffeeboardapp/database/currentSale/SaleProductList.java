package com.lewickiy.coffeeboardapp.database.currentSale;

import com.lewickiy.coffeeboardapp.database.discount.Discount;

import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.discounts;

public class SaleProductList {
    public static ArrayList<SaleProduct> currentSaleProducts = new ArrayList<>();
    public static void addSaleProductsList(int saleProdId, int prodSaleId, String prodName, double priceProdSale, int discountId, int discount, int amountProdSale, double sumProdSale) {
        currentSaleProducts.add(new SaleProduct(saleProdId, prodSaleId, prodName, priceProdSale, discountId, discount, amountProdSale, sumProdSale));
    }

    public static void addProductToArray(int positionsCount, SaleProduct currentProduct) {
        currentProduct.setSum((currentProduct.getPrice()
                - (currentProduct.getPrice() * currentProduct.getDiscount() / 100))
                * currentProduct.getAmount());

        if (positionsCount == 0) {
            addProductIf(currentProduct);
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
                                    * currentSaleProducts.get(i).getDiscount() / 100))
                            * currentSaleProducts.get(i).getAmount());
                    b = false;
                    break;
                }
            }

            if (b == true) {
                addProductIf(currentProduct);
            }
        }
    }

    static void addProductIf(SaleProduct currentProduct) {
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