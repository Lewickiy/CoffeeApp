package com.lewickiy.coffeeboardapp.controllers.actions;

import com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentType;
import javafx.scene.control.Button;

import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.paymentTypes;

public class PaymentTypeNameButton {
    public static void paymentTypeNameButton(Button[] buttons) {
        int count = 0;
        for (PaymentType paymentType : paymentTypes) {
            buttons[count].setAccessibleText(String.valueOf(paymentType.getPaymentTypeId()));
            buttons[count].setText(paymentType.getPaymentType());
            count++;
        }
    }
}