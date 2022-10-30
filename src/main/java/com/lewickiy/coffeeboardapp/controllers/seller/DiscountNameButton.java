package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.database.discount.Discount;
import javafx.scene.control.Button;

import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.discounts;

public class DiscountNameButton {
    public static void discountNameButtons(ArrayList<Button> discountButtons) {
        System.out.println("Start naming discount Buttons");
        int count = 0;
        System.out.println(discounts);
        for (Discount discount : discounts) {
            System.out.println(discount.getDiscount() + " %");
            System.out.println(discount.isActive() + " ...");
            if (discount.isActive()) {
                System.out.println(discount.getDiscount() + " сама скидка");
                discountButtons.get(count).setAccessibleText(String.valueOf(discount.getDiscountId()));
                discountButtons.get(count).setText(discount.getDiscount() + "%");
                discountButtons.get(count).setVisible(true);
                System.out.println(discountButtons.get(count).getText());
                count++;
            }
        }
    }
}