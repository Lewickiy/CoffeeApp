package com.lewickiy.coffeeboardapp.database.discount;

public class Discount {
    private int discountId;
    private int discount;
    private boolean active;

    public Discount(int discountId, int discount, boolean active) {
        this.discountId = discountId;
        this.discount = discount;
        this.active = active;
    }

    public Discount(int discount) {
        this.discount = discount;
    }

    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}