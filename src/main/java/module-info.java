module com.lewickiy.coffeeboardapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.lewickiy.coffeeboardapp to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.administrator to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.seller to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.info to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.idgenerator to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.dao.todaySales to javafx.base;
    exports com.lewickiy.coffeeboardapp;
    exports com.lewickiy.coffeeboardapp.entities.outlet;
    exports com.lewickiy.coffeeboardapp.entities.discount;
    exports com.lewickiy.coffeeboardapp.entities.product;
    exports com.lewickiy.coffeeboardapp.entities.user;
    exports com.lewickiy.coffeeboardapp.idgenerator;
    opens com.lewickiy.coffeeboardapp.controllers.seller.actions to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.actions to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.entities.product to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login.actions to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login.actions.worktable to javafx.fxml;
    exports com.lewickiy.coffeeboardapp.entities.saleproduct;
    exports com.lewickiy.coffeeboardapp.entities.currentsale;
    exports com.lewickiy.coffeeboardapp.dao;
    exports com.lewickiy.coffeeboardapp.dao.query;
    exports com.lewickiy.coffeeboardapp.entities.productcategory;
    opens com.lewickiy.coffeeboardapp.entities.productcategory to javafx.fxml;
    exports com.lewickiy.coffeeboardapp.dao.connector;
}