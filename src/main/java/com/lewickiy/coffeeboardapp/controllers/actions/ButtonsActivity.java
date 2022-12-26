package com.lewickiy.coffeeboardapp.controllers.actions;

import javafx.scene.control.Button;

import java.util.ArrayList;

public class ButtonsActivity {
    /**
     * Change button accessibility<br>
     * @param buttons accepted parameter - ArrayList of buttons<br>
     * @param res boolean value answering the button availability question
     */
    public static void buttonsIsDisable(ArrayList<Button> buttons, boolean res) {
        for (Button button : buttons) {
            button.setDisable(res);
        }
    }
    public static void buttonsIsDisable(ArrayList<Button> buttons1, ArrayList<Button> buttons2, boolean res1, boolean res2) {
        for (Button button : buttons1) {
            button.setDisable(res1);
        }
        for (Button button : buttons2) {
            button.setDisable(res2);
        }
    }
}