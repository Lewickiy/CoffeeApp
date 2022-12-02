module com.lewickiy.coffeeboardapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.lewickiy.coffeeboardapp to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.administrator to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.seller to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.idgenerator to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.database.local.todaySales to javafx.base;
    exports com.lewickiy.coffeeboardapp;
    exports com.lewickiy.coffeeboardapp.database;
    exports com.lewickiy.coffeeboardapp.database.outlet;
    exports com.lewickiy.coffeeboardapp.database.discount;
    exports com.lewickiy.coffeeboardapp.database.product;
    exports com.lewickiy.coffeeboardapp.entities.user;
    exports com.lewickiy.coffeeboardapp.idgenerator;
    opens com.lewickiy.coffeeboardapp.controllers.seller.actions to javafx.fxml;
    exports com.lewickiy.coffeeboardapp.database.connection;
    opens com.lewickiy.coffeeboardapp.controllers.actions to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.database.product to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login.actions to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login.actions.worktable to javafx.fxml;
    exports com.lewickiy.coffeeboardapp.entities.saleProduct;
    exports com.lewickiy.coffeeboardapp.entities.currentSale;
}