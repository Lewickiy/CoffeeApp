package com.lewickiy.coffeeboardapp.entities.paymenttype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentType {
    private int paymentTypeId;
    private String paymentType;

    @Override
    public String toString() {
        return paymentTypeId + " - payment type id, " + paymentType + " - payment type";
    }
}