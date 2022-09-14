module com.lewickiy.coffeeboardapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.lewickiy.coffeeboardapp to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.seller to javafx.fxml;
    exports com.lewickiy.coffeeboardapp;
    exports com.lewickiy.coffeeboardapp.database;
    exports com.lewickiy.coffeeboardapp.database.currentSale;
    exports com.lewickiy.coffeeboardapp.database.outlet;
    exports com.lewickiy.coffeeboardapp.database.discount;
    exports com.lewickiy.coffeeboardapp.database.product;
    exports com.lewickiy.coffeeboardapp.database.user;
}