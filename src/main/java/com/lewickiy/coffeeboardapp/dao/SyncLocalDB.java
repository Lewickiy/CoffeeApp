package com.lewickiy.coffeeboardapp.dao;

import com.lewickiy.coffeeboardapp.entities.discount.Discount;
import com.lewickiy.coffeeboardapp.entities.outlet.Outlet;
import com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentType;
import com.lewickiy.coffeeboardapp.entities.product.Product;
import com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategory;
import com.lewickiy.coffeeboardapp.entities.user.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.query.Query.*;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.users;

public class SyncLocalDB {
    static final String LOCAL_DB = "local_database";
    static final String NETWORK_DB = "network_database";

    public static void syncUsersList(Connection networkCon, Connection localCon) throws SQLException {
        int count = 0;
        ResultSet resultSet = selectAllFromSql(networkCon, NETWORK_DB,"user");
        while(resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String login = resultSet.getString("login");
            String password = resultSet.getString("password");
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String patronymic = resultSet.getString("patronymic");
            Date birthday = resultSet.getDate("birthday");
            String phone = resultSet.getString("phone");
            boolean administrator = resultSet.getBoolean("administrator");
            boolean activeStuff = resultSet.getBoolean("active_stuff");
            users.add(new User(userId, login, password, firstName, lastName, patronymic, birthday, phone, administrator, activeStuff));
            count++;
        }
        LOGGER.log(Level.INFO,count + " users loaded to users array from network database");
        resultSet.close();

        deleteFromLocalSql(localCon, "user");

        LOGGER.log(Level.INFO,"Start loading users to sqlite from array");
        for (User user : users) {
            insertToSql(localCon, LOCAL_DB,"user", "user_id, "
                    + "login, "
                    + "first_name, "
                    + "last_name, "
                    + "patronymic, "
                    + "birthday, "
                    + "phone, "
                    + "password, "
                    + "administrator, "
                    + "active_stuff) VALUES ('"
                    + user.getUserId() + "', '"
                    + user.getLogin() + "', '"
                    + user.getFirstName() + "', '"
                    + user.getLastName() + "', '"
                    + user.getPatronymic() + "', '"
                    + user.getBirthday() + "', '"
                    + user.getPhone() + "', '"
                    + user.getPassword() + "', '"
                    + decodeIntBoolean(user.isAdministrator()) + "', '"
                    + decodeIntBoolean(user.isActiveStuff()) + "'");
        }
        LOGGER.log(Level.INFO,"Users added to SQLite");
        users.clear();
        LOGGER.log(Level.INFO,"Users array cleared");
    }

    public static void syncOutletsList(Connection networkCon, Connection localCon) throws SQLException {
        ResultSet resultSet = selectAllFromSql(networkCon, NETWORK_DB,"outlet");
        while(resultSet.next()) {
            int outletId = resultSet.getInt("outlet_id");
            String outlet = resultSet.getString("outlet");
            outlets.add(new Outlet(outletId, outlet));

        }
        resultSet.close();

        deleteFromLocalSql(localCon, "outlet");

        for (Outlet outlet : outlets) {
            insertToSql(localCon, LOCAL_DB,"outlet", "outlet_id, "
                    + "outlet) VALUES ('"
                    + outlet.getOutletId() + "', '"
                    + outlet.getOutlet() + "'");
        }
        outlets.clear();
    }

    public static void syncPaymentTypesList(Connection networkCon, Connection localCon) throws SQLException {
        ResultSet resultSet = selectAllFromSql(networkCon, NETWORK_DB,"paymenttype");
        while(resultSet.next()) {
            int paymenttypeId = resultSet.getInt("paymenttype_id");
            String paymenttype = resultSet.getString("paymenttype");
            paymentTypes.add(new PaymentType(paymenttypeId, paymenttype));
        }
        resultSet.close();

        deleteFromLocalSql(localCon, "paymenttype");

        for (PaymentType paymentType : paymentTypes) {
            insertToSql(localCon, LOCAL_DB,"paymenttype", "paymenttype_id, "
                    + "paymenttype) VALUES ('"
                    + paymentType.getPaymentTypeId() + "', '"
                    + paymentType.getPaymentType() + "'");
        }
        paymentTypes.clear();
    }

    public static void syncProductCategoriesList(Connection networkCon, Connection localCon) throws SQLException {
        ResultSet resultSet = selectAllFromSql(networkCon, NETWORK_DB,"product_category");
        while(resultSet.next()) {
            int productCategoryId = resultSet.getInt("product_category_id");
            String productCategory = resultSet.getString("product_category");
            productCategories.add(new ProductCategory(productCategoryId, productCategory));
        }
        resultSet.close();

        deleteFromLocalSql(localCon, "product_category");

        for (ProductCategory productCategory : productCategories) {
            insertToSql(localCon, LOCAL_DB,"product_category", "product_category_id, "
                    + "product_category) VALUES ('"
                    + productCategory.getProductCategoryId() + "', '"
                    + productCategory.getProductCategory() + "'");
        }
        productCategories.clear();
    }

    public static void syncProductsList(Connection networkCon, Connection localCon) throws SQLException {
        ResultSet resultSet = selectProductsFromNDB(networkCon);
        while(resultSet.next()) {
            int productId = resultSet.getInt("price_id");
            String product = resultSet.getString("product");
            String description = resultSet.getString("description");
            int numberOfUnit = resultSet.getInt("number_of_unit");
            String unitOfMeasurement = resultSet.getString("unit_of_measurement");
            int category = resultSet.getInt("product_category_id");
            double price = resultSet.getDouble("price");
            boolean fixPrice = resultSet.getBoolean("fix_price");
            products.add(new Product(productId, product, description, numberOfUnit, unitOfMeasurement, category, price, fixPrice));
        }
        resultSet.close();

        deleteFromLocalSql(localCon, "product");

        for (Product product : products) {
            insertToSql(localCon, LOCAL_DB,"product", "product_id, "
                    + "product, "
                    + "description, "
                    + "number_of_unit, "
                    + "unit_of_measurement, "
                    + "product_category_id, "
                    + "price, "
                    + "fix_price) VALUES ('"
                    + product.getProductId() + "', '"
                    + product.getProduct() + "', '"
                    + product.getDescription() + "', '"
                    + product.getNumberOfUnit() + "', '"
                    + product.getUnitOfMeasurement() + "', '"
                    + product.getCategory() + "', '"
                    + product.getPrice() + "', '"
                    + decodeIntBoolean(product.isFixPrice()) + "'");
        }
        products.clear();
    }

    public static void syncDiscountsList(Connection networkCon, Connection localCon) throws SQLException {
        ResultSet resultSet = selectAllFromSql(networkCon, NETWORK_DB,"discount");
        while(resultSet.next()) {
            int discountId = resultSet.getInt("discount_id");
            int discount = resultSet.getInt("discount");
            boolean active = resultSet.getBoolean("active");

            discounts.add(new Discount(discountId, discount, active));
        }
        resultSet.close();

        deleteFromLocalSql(localCon, "discount");

        for (Discount discount : discounts) {
            insertToSql(localCon, LOCAL_DB,"discount", "discount_id, "
                    + "discount, "
                    + "active) VALUES ('"
                    + discount.getDiscountId() + "', '"
                    + discount.getDiscount() + "', '"
                    + decodeIntBoolean(discount.isActive()) + "'");
        }
        discounts.clear();
    }
}