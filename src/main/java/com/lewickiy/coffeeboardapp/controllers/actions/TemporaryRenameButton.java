package com.lewickiy.coffeeboardapp.controllers.actions;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class TemporaryRenameButton {
    /**
     * Temporarily renames the button.<br>
     * @param button - button for temporarily rename.<br>
     * @param tempText - temporarily name.<br>
     * @param sec takes a time in seconds as a parameter.<br>
     */
    public static void tempRenameButton(Button button, String tempText, int sec) {
        String firstName = button.getText();
        int millis = sec * 1000;
        button.setFont(Font.font("Courier New", FontWeight.NORMAL, 30));
        button.setText(tempText);
        Thread renameCashReceiptButton = new Thread(() -> {

            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                button.setText(firstName);
                button.setFont(Font.font("Courier New", FontWeight.NORMAL, 38));
            });
        });   renameCashReceiptButton.setDaemon(true);
        renameCashReceiptButton.start();
    }
}