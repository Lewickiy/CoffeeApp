package com.lewickiy.coffeeboardapp.controllers.actions;

import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;
import com.lewickiy.coffeeboardapp.entities.product.Product;
import javafx.scene.control.Button;

import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;

public class DiscountAction {
    /**
     * This method discounts a product that is not marked as a fix price product.<br>
     * @param button takes a button with a discount as a parameter and takes the accessible text<br>
     *               from it as the discount id.<br>
     */
    public static void makeDiscount(Button button) {
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            for (Product product : products) {
                if (!product.isFixPrice() && product.getProductId() == currentSaleProduct.getProductId()) {
                    currentSaleProduct.setDiscountId(Integer.parseInt(button.getAccessibleText()));
                    for (com.lewickiy.coffeeboardapp.entities.discount.Discount discount : discounts) {
                        if (currentSaleProduct.getDiscountId() == discount.getDiscountId()) {
                            currentSaleProduct.setDiscount(discount.getDiscount());
                            currentSaleProduct.setSum((currentSaleProduct.getPrice()
                            - (currentSaleProduct.getPrice() * currentSaleProduct.getDiscount()
                            / 100)) * currentSaleProduct.getAmount());
                        }
                    }
                }
            }
        }
    }
}