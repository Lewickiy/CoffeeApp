package com.lewickiy.coffeeboardapp.dao.connector;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.connectionToNDB;

public class NetworkIndicator {
    public static void isOnline(Label networkIndicatorLabel, Circle networkIndicator) {
        Timeline syncTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        event -> {
                            if (connectionToNDB) {
                                networkIndicatorLabel.setText("в сети  ");
                                networkIndicator.setFill(Color.GREEN);
                            } else {
                                networkIndicatorLabel.setText("не в сети  ");
                                networkIndicator.setFill(Color.GOLD);
                            }
                        }));
        syncTimeLine.setCycleCount(Timeline.INDEFINITE);
        syncTimeLine.play();
    }
}