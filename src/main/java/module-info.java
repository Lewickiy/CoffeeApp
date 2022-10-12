module com.lewickiy.coffeeboardapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.lewickiy.coffeeboardapp to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.login to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.controllers.seller to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.idgenerator to javafx.fxml;
    opens com.lewickiy.coffeeboardapp.database.local.todaySales to javafx.base;
    exports com.lewickiy.coffeeboardapp;
    exports com.lewickiy.coffeeboardapp.database;
    exports com.lewickiy.coffeeboardapp.database.currentSale;
    exports com.lewickiy.coffeeboardapp.database.outlet;
    exports com.lewickiy.coffeeboardapp.database.discount;
    exports com.lewickiy.coffeeboardapp.database.product;
    exports com.lewickiy.coffeeboardapp.database.user;
    exports com.lewickiy.coffeeboardapp.idgenerator;
}