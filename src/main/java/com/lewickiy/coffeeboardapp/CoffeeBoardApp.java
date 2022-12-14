package com.lewickiy.coffeeboardapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author - Lewickiy Anatoliy
 * @version - start0.9
 */

public class CoffeeBoardApp extends Application {
    //TODO вчерашняя смена не закрыта? Предложить закрыть.
    //TODO может идентификация пользователя по лицу?
    //TODO таблица со всеми продажами (строки) должна нажиматься без предварительного нажатия на заголовки столбцов
    public static Logger LOGGER;
    static {
        try(FileInputStream ins = new FileInputStream("log.config")){
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(CoffeeBoardApp.class.getName());
        }catch (Exception ignore){
            ignore.printStackTrace();
        }
    }
    public static boolean connectionToNDB;

    @Override
    public void start(Stage stage) throws IOException {
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