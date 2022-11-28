package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.ArrayList;

public class ButtonsOnGridPane {
    /**
     * Метод, размещающий кнопки в GridPane по столбцам сверху-вниз, слева-направо (При выборе Direction.VERTICAL)
     * и слева-направо, сверху-вниз при выборе Direction.HORIZONTAL)<br>
     * @param var - выбор из ENUM Direction.VERTICAL/HORIZONTAL.
     * @param gridPane - на которую необходимо разместить кнопки<br>
     * @param buttons - ArrayList Buttons которых необходимо разместить в GridPane
     * @param event - присваиваемое кнопкам мероприятие.
     */
    public static void buttonsOnGridPane(Direction var, GridPane gridPane, ArrayList<Button> buttons, EventHandler<ActionEvent> event) {
        //TODO более универсальную систему, которая принимает значения horizontal или vertical,
        // в зависимости от этого выстраивает модель размещения. Сейчас не подходит для горизонтального размещения
        // NUMBER_BUTTONS, например.
        if(var.equals(Direction.VERTICAL)) {
            int countP = 0;
            for (int column = 0; column < gridPane.getColumnCount(); column++) {
                for (int row = 0; row < gridPane.getRowCount(); row++) {
                    Button productButton = new Button();
                    buttons.add(countP, productButton);
                    int finalProdButtonsCount = countP;

                    buttons.get(countP).layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
                            buttons.get(finalProdButtonsCount).setFont(
                                    Font.font(Math.sqrt(newValue.getHeight() * 1.5))));

                    buttons.get(countP).setWrapText(true);
                    buttons.get(countP).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
                    buttons.get(countP).setPrefSize(91.0, 91.0);
                    buttons.get(countP).setVisible(false);
                    GridPane.setConstraints(buttons.get(countP), column, row);
                    gridPane.getChildren().add(buttons.get(countP));
                    buttons.get(countP).setOnAction(event);
                    countP++;
                }
            }
        } else if (var.equals(Direction.HORIZONTAL)) {
            for (int column = 0; column < gridPane.getColumnCount(); column++) {
                Button numberButton = new Button();
                buttons.add(column, numberButton);
                int finalI = column;
                buttons.get(column).layoutBoundsProperty().addListener((observable, oldValue, newValue) -> buttons.get(finalI).setFont(Font.font(Math.sqrt(newValue.getHeight() * 10))));
                buttons.get(column).setWrapText(true);
                buttons.get(column).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
                buttons.get(column).setPrefSize(91.0, 91.0);
                buttons.get(column).setVisible(true);
                GridPane.setConstraints(buttons.get(column), column, 0);
                gridPane.getChildren().add(buttons.get(column));
                buttons.get(column).setOnAction(event);
                //TODO вынести метод именования цифровых кнопок в отдельный класс. Как прочие кнопки.
                if (column < 9) {
                    buttons.get(column).setText(String.valueOf(column + 1));
                    buttons.get(column).setAccessibleText(String.valueOf(column + 1));
                } else {
                    buttons.get(column).setText("0");
                    buttons.get(column).setAccessibleText("0");
                }
            }
        }
    }
}
