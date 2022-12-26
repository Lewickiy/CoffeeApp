package com.lewickiy.coffeeboardapp.dao;

import com.lewickiy.coffeeboardapp.dao.connector.DataBaseEnum;
import com.lewickiy.coffeeboardapp.entities.discount.Discount;
import com.lewickiy.coffeeboardapp.entities.outlet.Outlet;
import com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentType;
import com.lewickiy.coffeeboardapp.entities.product.Product;
import com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategory;
import com.lewickiy.coffeeboardapp.entities.user.User;

import java.sql.*;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;
import static com.lewickiy.coffeeboardapp.dao.query.Query.*;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.dao.helper.FalseTrueDecoderDB.decodeIntBoolean;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.users;

public class SyncLocalDB {
    static final String NETWORK_DB = "network_database";
    public static void syncUsersList(Connection networkCon) throws SQLException {
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

        deleteFromLocalSql("user");

        LOGGER.log(Level.INFO,"Start loading users to sqlite from array");
        for (User user : users) {
            insertToSql(DataBaseEnum.LOCAL_DB,"user", "user_id, "
                    + "login, "
                    + "first_name, "
                    + "last_name, "
                    + "patronymic, "
                    + "birthday, "
                    + "phone, "
                    + "password, "
                    + "administrator, "
                    + "active_stuff) VALUES ('"
                    + user.getId() + "', '"
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

    public static void syncOutletsList(Connection networkCon) throws SQLException {
        ResultSet resultSet = selectAllFromSql(networkCon, NETWORK_DB,"outlet");
        while(resultSet.next()) {
            int outletId = resultSet.getInt("outlet_id");
            String outlet = resultSet.getString("outlet");
            outlets.add(new Outlet(outletId, outlet));

        }
        resultSet.close();

        deleteFromLocalSql("outlet");

        for (Outlet outlet : outlets) {
            insertToSql(DataBaseEnum.LOCAL_DB,"outlet", "outlet_id, "
                    + "outlet) VALUES ('"
                    + outlet.getOutletId() + "', '"
                    + outlet.getOutlet() + "'");
        }
        outlets.clear();
    }

    public static void syncPaymentTypesList() throws SQLException {
        Connection con = getConnectionNDB();
        if (con != null) {
            ResultSet resultSet = selectAllFromSql(con, NETWORK_DB, "paymenttype");
            while (resultSet.next()) {
                int paymenttypeId = resultSet.getInt("paymenttype_id");
                String paymenttype = resultSet.getString("paymenttype");
                paymentTypes.add(new PaymentType(paymenttypeId, paymenttype));
            }
            resultSet.close();

            deleteFromLocalSql("paymenttype");

            for (PaymentType paymentType : paymentTypes) {
                insertToSql(DataBaseEnum.LOCAL_DB, "paymenttype", "paymenttype_id, "
                        + "paymenttype) VALUES ('"
                        + paymentType.getPaymentTypeId() + "', '"
                        + paymentType.getPaymentType() + "'");
            }
            paymentTypes.clear();
        } else {
            //TODO
            System.out.println("oups");
        }
    }

    public static void syncProductCategoriesList() throws SQLException {
        Connection con = getConnectionNDB();
        if (con != null) {
            ResultSet resultSet = selectAllFromSql(con, NETWORK_DB, "product_category");
            while (resultSet.next()) {
                int productCategoryId = resultSet.getInt("product_category_id");
                String productCategory = resultSet.getString("product_category");
                productCategories.add(new ProductCategory(productCategoryId, productCategory));
            }
            resultSet.close();
            con.close();

            deleteFromLocalSql("product_category");

            for (ProductCategory productCategory : productCategories) {
                insertToSql(DataBaseEnum.LOCAL_DB, "product_category", "product_category_id, "
                        + "product_category) VALUES ('"
                        + productCategory.getProductCategoryId() + "', '"
                        + productCategory.getProductCategory() + "'");
            }
            productCategories.clear();
        } else {
            //TODO
            System.out.println("oups");
        }
    }

    public static void syncProductsList() throws SQLException {
        Connection con = getConnectionNDB();
        if (con != null) {
            Statement statement = con.createStatement();
            String query = "SELECT " +
                    "price.price_id AS price_id, " +
                    "product.product AS product, " +
                    "product.description AS description, " +
                    "unit.amount_unit AS number_of_unit, " +
                    "unit.unit AS unit_of_measurement, " +
                    "product.product_category_id AS product_category_id, " +
                    "price.price AS price, " +
                    "price.fix_price AS fix_price " +
                    "FROM " +
                    "product " +
                    "JOIN " +
                    "unit " +
                    "ON " +
                    "product.product_id = unit.product_id " +
                    "JOIN " +
                    "price " +
                    "ON " +
                    "price.unit_id = unit.unit_id " +
                    "WHERE unit.outlet_id = 5 " +
                    "ORDER BY product.product";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
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
            con.close();
        }

        deleteFromLocalSql("product");

        for (Product product : products) {
            insertToSql(DataBaseEnum.LOCAL_DB,"product", "product_id, "
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

    public static void syncDiscountsList() throws SQLException {
        Connection con = getConnectionNDB();
        if (con != null) {
            ResultSet resultSet = selectAllFromSql(con, NETWORK_DB, "discount");
            while (resultSet.next()) {
                int discountId = resultSet.getInt("discount_id");
                int discount = resultSet.getInt("discount");
                boolean active = resultSet.getBoolean("active");

                discounts.add(new Discount(discountId, discount, active));
            }
            resultSet.close();
            con.close();

            deleteFromLocalSql("discount");

            for (Discount discount : discounts) {
                insertToSql(DataBaseEnum.LOCAL_DB,"discount", "discount_id, "
                        + "discount, "
                        + "active) VALUES ('"
                        + discount.getDiscountId() + "', '"
                        + discount.getDiscount() + "', '"
                        + decodeIntBoolean(discount.isActive()) + "'");
            }
            discounts.clear();
        } else {
            //TODO
            System.out.println("oups");
        }
    }
}