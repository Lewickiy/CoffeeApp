package com.lewickiy.coffeeboardapp.dao.sync;

import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.*;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.createPaymentTypeList;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.createProductsList;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.createProductCategoriesList;

public class BusinessData {
    public static void syncBusinessData() {
        syncProductsList();
        syncProductCategoriesList();
        syncPaymentTypesList();
        syncDiscountsList();

        createProductsList();
        createProductCategoriesList();
        createPaymentTypeList();
        createDiscountList();
    }
}