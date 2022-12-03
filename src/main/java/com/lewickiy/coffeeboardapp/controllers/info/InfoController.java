package com.lewickiy.coffeeboardapp.controllers.info;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.seller.SellerController.startSync;
import static com.lewickiy.coffeeboardapp.dao.info.Info.info;
import static com.lewickiy.coffeeboardapp.database.connection.DatabaseConnector.getConnection;

public class InfoController {
    @FXML
    private Label infoTextLabel;
    @FXML
    private Button okInfoButton;
    @FXML
    private Button laterButton;

    @FXML
    void okInfoButtonOnAction() {
        Stage stage = (Stage) okInfoButton.getScene().getWindow();
        try {
            Connection con = getConnection("network_database");
            String update = "UPDATE info_for_pos SET delivered = ? WHERE info_for_pos_id = " + info.getInfoId();
            PreparedStatement prepareStatement = con.prepareStatement(update);
            prepareStatement.setInt(1, 1);
            prepareStatement.executeUpdate();
            prepareStatement.close();
            con.close();
            info = null;
            startSync = true;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING,"Failed to connect to the network database to send information that the message was read by the user");
            info = null;
            startSync = true;

        }
        stage.close();
    }
    @FXML
    void laterButtonOnAction() {
        Stage stage = (Stage) laterButton.getScene().getWindow();
        stage.close();
        startSync = true;
    }

    @FXML
    void initialize() {
        infoTextLabel.setText(info.getMessage());
    }
}