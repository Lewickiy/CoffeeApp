package com.lewickiy.coffeeboardapp.controllers.administrator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesList.*;

public class AdministratorController {
    @FXML
    private Label yesterdayAllLabel;
    @FXML
    private Label yesterdayCashLabel;
    @FXML
    private Label yesterdayCardLabel;
    //_______________________________
    @FXML
    private Label todayAllLabel;
    @FXML
    private Label todayCashLabel;
    @FXML
    private Label todayCardLabel;

    @FXML
    void initialize() {
        todayAllLabel.setText(String.valueOf(sumAll()));
        todayCashLabel.setText(String.valueOf(sumCash()));
        todayCardLabel.setText(String.valueOf(sumCard()));
    }
}
