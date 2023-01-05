package com.lewickiy.coffeeboardapp.entities.discount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
    private int discountId;
    private int discount;
    private boolean active;
}