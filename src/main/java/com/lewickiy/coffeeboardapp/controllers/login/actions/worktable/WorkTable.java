package com.lewickiy.coffeeboardapp.controllers.login.actions.worktable;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class WorkTable {
    public static void enterToWorkTable(WorkTableChoice workTableChoice, Button button) {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        System.out.println("Разрешение экрана: " + bounds.getWidth() + "х" + bounds.getHeight());
        System.out.println("Рабочее пространство: " + visualBounds.getWidth() + "х" + visualBounds.getHeight());
        String fxml = "";
        boolean setMaximized = false;
        if (workTableChoice.equals(WorkTableChoice.ADMINISTRATOR)) {
            fxml = "administrator.fxml";
            setMaximized = true;
        } else if (workTableChoice.equals(WorkTableChoice.LOGIN)) {
            fxml = "login.fxml";
        } else if (workTableChoice.equals(WorkTableChoice.SELLER)) {
            setMaximized = true;
            fxml = "seller.fxml";
        }

        Stage stageToClose = (Stage) button.getScene().getWindow();
        Stage stageToOpen = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource(fxml));

        Scene scene;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stageToOpen.initStyle(StageStyle.UNDECORATED);
        stageToOpen.setScene(scene);
        stageToOpen.setTitle("CoffeeApp");
        stageToOpen.setMaximized(setMaximized);
        stageToOpen.show();
        stageToClose.close();
    }
    public static void enterToWorkTable(WorkTableChoice workTableChoice) {
        String fxml = "";
        if (workTableChoice.equals(WorkTableChoice.INFO)) {
            fxml = "info.fxml";
        }
        Stage infoStage = new Stage();
        FXMLLoader fxmlInfoLoader = new FXMLLoader(CoffeeBoardApp.class.getResource(fxml));

        Scene infoScene;
        try {
            infoScene = new Scene(fxmlInfoLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        infoStage.initStyle(StageStyle.UNDECORATED);
        infoStage.alwaysOnTopProperty();
        infoStage.setScene(infoScene);
        infoStage.setTitle("CoffeeApp");
        infoStage.setMaximized(false);
        infoStage.setAlwaysOnTop(true);
        infoStage.show();
    }
}
