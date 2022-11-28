package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;

public class ClockThread {
    public static void startClockThread(Label clockLabel, long updateTimeSec) {
        Thread clockThread = new Thread(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            while (true) {
                try {
                    Thread.sleep(updateTimeSec * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String time = simpleDateFormat.format(new java.util.Date());
                Platform.runLater(() -> clockLabel.setText(time));
            }
        });   clockThread.setDaemon(true);
        clockThread.start();
    }
}