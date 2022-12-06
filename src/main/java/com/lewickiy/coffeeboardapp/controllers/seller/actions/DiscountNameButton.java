package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import com.lewickiy.coffeeboardapp.entities.discount.Discount;
import javafx.scene.control.Button;

import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;

public class DiscountNameButton {
    public static void discountNameButtons(ArrayList<Button> discountButtons) {
        int count = 0;
        for (Discount discount : discounts) {
            if (discount.isActive()) {
                discountButtons.get(count).setAccessibleText(String.valueOf(discount.getDiscountId()));
                discountButtons.get(count).setText(discount.getDiscount() + "%");
                discountButtons.get(count).setVisible(true);
                count++;
            }
        }
    }
}
