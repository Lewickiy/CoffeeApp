package com.lewickiy.coffeeboardapp.controllers.actions;

import com.lewickiy.coffeeboardapp.entities.discount.Discount;
import javafx.scene.control.Button;

import java.util.List;

import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;

public class DiscountNameButton {
    public static void discountNameButtons(List<Button> discountButtons) {
        int count = 0;
        for (Discount discount : discounts) {
            if (discount.isActive()) {
                discountButtons.get(count).setAccessibleText(String.valueOf(discount.getDiscountId()));
                discountButtons.get(count).setText(discount.getDiscount() + "%");
                discountButtons.get(count).setVisible(true);
                discountButtons.get(count).setFocusTraversable(false);
                count++;
            }
        }
    }
}
