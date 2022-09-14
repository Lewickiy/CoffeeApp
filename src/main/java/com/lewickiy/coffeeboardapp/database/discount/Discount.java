package com.lewickiy.coffeeboardapp.database.discount;

public class Discount {
    private int discountId;
    private int discount;

    public Discount(int discountId, int discount) {
        this.discountId = discountId;
        this.discount = discount;
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
}
