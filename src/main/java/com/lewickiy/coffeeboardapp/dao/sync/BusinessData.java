package com.lewickiy.coffeeboardapp.dao.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.*;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.createPaymentTypeList;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.createProductsList;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.createProductCategoriesList;

public class BusinessData {
    /**
     * Синхронизация сетевой базы данных с локальной:
     * - продукты
     * - категории
     * - типы оплаты
     * - скидки с последующим созданием ArrayLists для работы с ними.
     * В случае отсутствия подключения, работа продолжается с локальной базой данных.
     * @throws SQLException -...
     */
    public static void syncBusinessData() throws SQLException {
        Connection conNDB = getConnectionNDB();
        Connection conLDB = getConnectionLDB();
        if (conNDB != null) {
            conNDB.close();
            syncProductsList();
            syncProductCategoriesList();
            syncPaymentTypesList();
            syncDiscountsList();

            createProductsList(conLDB);
            createProductCategoriesList(conLDB);
            createPaymentTypeList(conLDB);
            createDiscountList(conLDB);

            if(conLDB != null) {
                conLDB.close();
            }
            LOGGER.log(Level.FINE, "Synchronization completed successfully");
        } else {
            LOGGER.log(Level.WARNING,"When starting to synchronize with the network database at Seller desktop startup, it was not possible to connect to the network. Data is taken from local database");
            createProductsList(conLDB);
            createProductCategoriesList(conLDB);
            createPaymentTypeList(conLDB);
            createDiscountList(conLDB);
            //*
            LOGGER.log(Level.INFO, "Further work may be carried out with outdated data");
            if (conLDB != null) {
                conLDB.close();
            }
        }
    }
}