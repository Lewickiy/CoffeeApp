package com.lewickiy.coffeeboardapp.entities.outlet;

public class Outlet {
    public static Outlet currentOutlet;
    private final int outletId;
    private String outlet;

    public Outlet(int outletId, String outlet) {
        this.outletId = outletId;
        this.outlet = outlet;
    }
    public int getOutletId() {
        return outletId;
    }
    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }
    @Override
    public String toString() {
        return outlet;
    }
}
