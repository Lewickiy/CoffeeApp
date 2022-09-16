package com.lewickiy.coffeeboardapp.database.paymentType;

public class PaymentType {
    private int paymentTypeId;
    private String paymentType;

    public PaymentType(int paymentTypeId, String paymentType) {
        this.paymentTypeId = paymentTypeId;
        this.paymentType = paymentType;
    }

    public int getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}