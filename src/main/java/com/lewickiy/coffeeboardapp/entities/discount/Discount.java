package com.lewickiy.coffeeboardapp.entities.discount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Discount {
    private int discountId;
    private int discount;
    private boolean active;
}