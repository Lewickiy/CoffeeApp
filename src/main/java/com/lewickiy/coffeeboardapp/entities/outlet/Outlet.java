package com.lewickiy.coffeeboardapp.entities.outlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Outlet {
    public static Outlet currentOutlet;
    private final int outletId;
    private String outlet;

    @Override
    public String toString() {
        return outlet;
    }
}