package com.lewickiy.coffeeboardapp.controllers.administrator;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;

public class AdministratorController {

    //TODO

    @FXML
    private Label clockLabel;
    @FXML
    void initialize() {
        Thread clockThread = new Thread(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String time = simpleDateFormat.format(new java.util.Date());
                Platform.runLater(() -> clockLabel.setText(time));
            }
        });   clockThread.setDaemon(true);
        clockThread.start();
        //TODO
    }
}
