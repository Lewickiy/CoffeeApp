package com.lewickiy.coffeeboardapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import static com.lewickiy.coffeeboardapp.database.local.LocalBase.createLocalDb;

/**
 * @Author - Lewickiy Anatoliy
 * @Version - start0.3
 */

public class CoffeeBoardApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        createLocalDb();
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("CoffeeApp");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}