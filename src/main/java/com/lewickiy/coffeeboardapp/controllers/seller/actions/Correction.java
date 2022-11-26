package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.product.Product;

import static com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.products;

public class Correction {
    /**
     * Amount adjustment for products in the current sale. When manually adjusting the check amount.<br>
     * @param startSumString takes the current value from sumLabel<br>
     * @param endSumString takes value from correctionTextField<br>
     */
    public static void correctionSum(String startSumString, String endSumString) {
        String endSum = endSumString.replace(',', '.');
        endSum = endSum.replace(" ", "");

        double endSumDouble = Double.parseDouble(endSum);
        double startSum = Double.parseDouble(startSumString);
        int productsInArray = 0;
        int fixPriceProducts = 0;

        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            for (Product product : products) {
                if (currentSaleProduct.getProductId() == product.getProductId() && product.isFixPrice()) {
                    fixPriceProducts = fixPriceProducts + currentSaleProduct.getAmount();
                }
            }
            productsInArray = productsInArray + currentSaleProduct.getAmount();
        }

        double correctionDiscount = (startSum - endSumDouble) / (productsInArray - fixPriceProducts);

        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            for (Product product : products) {
                if (currentSaleProduct.getProductId() == product.getProductId() && !product.isFixPrice()) {
                    currentSaleProduct.setSum((currentSaleProduct.getSum() - (correctionDiscount) * currentSaleProduct.getAmount()));
                }
            }
        }
    }
}