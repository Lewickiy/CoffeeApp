package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import javafx.scene.control.Button;

import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.dao.query.OpenCloseShift.updateShiftSql;
import static com.lewickiy.coffeeboardapp.dao.query.Query.deleteFromLocalSql;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.shiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.syncShiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.SyncCorrected.syncCorrectedSales;
import static com.lewickiy.coffeeboardapp.dao.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.dao.query.SyncSales.syncSales;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesList.todaySalesArrayList;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.users;

public class ShiftCloser {
    public static void closeShift(ArrayList<Button> PRODUCT_BUTTONS, ArrayList<Button> NUMBER_BUTTONS) {
        todaySalesArrayList.clear();
        updateShiftSql(true, 0.00);
        shiftLog(true);
        syncShiftLog();
        syncSales();
        syncSalesProduct();
        syncCorrectedSales();
        deleteFromLocalSql("sale_product");
        deleteFromLocalSql("sale");
        PRODUCT_BUTTONS.clear();
        NUMBER_BUTTONS.clear();
        productCategories.clear();
        currentSaleProducts.clear();
        products.clear();
        discounts.clear();
        paymentTypes.clear();
        outlets.clear();
        users.clear();
    }
}