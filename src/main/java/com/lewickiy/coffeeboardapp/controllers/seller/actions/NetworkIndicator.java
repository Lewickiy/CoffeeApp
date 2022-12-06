package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class NetworkIndicator {
    /**
     * Method toggle network status indicator changes<br>
     * networkIndicatorLabel and networkIndicator (online/offline)<br>
     * @param networkIndicatorLabel - Label with text "online/offline"<br>
     * @param networkIndicator - Circle change color "Green/Yellow"<br>
     * @param status Takes a boolean parameter as an answer to the question<br>
     * in the isOnline method name.
     */
    public static void isOnline(Label networkIndicatorLabel, Circle networkIndicator, boolean status) {
        if (status) {
            networkIndicatorLabel.setText("в сети");
            networkIndicator.setFill(Color.GREEN);
        } else {
            networkIndicatorLabel.setText("не в сети");
            networkIndicator.setFill(Color.GOLD);
        }
    }
}