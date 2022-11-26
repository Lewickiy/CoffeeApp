package com.lewickiy.coffeeboardapp.controllers.login;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import com.lewickiy.coffeeboardapp.database.outlet.Outlet;
import com.lewickiy.coffeeboardapp.database.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.local.SyncLocalDB.syncOutletsList;
import static com.lewickiy.coffeeboardapp.database.local.SyncLocalDB.syncUsersList;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.createOutletList;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.database.user.UserList.*;

public class LoginController {
    static ObservableList<Outlet> outletsObservableList = FXCollections.observableList(outlets);
    @FXML
    private AnchorPane outletChoicePane;
    @FXML
    private ChoiceBox<Outlet> outletChoiceBox;
    @FXML
    private Button loginButton, cancelButton, acceptOutletChoice;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessageLabel, networkIndicatorLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Circle networkIndicator;

    @FXML
    void initialize() throws SQLException, ParseException {
        LOGGER.log(Level.INFO,"Start Sync...");

        Connection conNetworkUserDB;
        try {
            conNetworkUserDB = getConnection("network_database");
            LOGGER.log(Level.FINE,"Connection to network database established");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Connection to network database failed");
            conNetworkUserDB = null;
        }

        Connection conLocalUserDB = getConnection("local_database");

        if (conNetworkUserDB != null) {
            isOnline(true);
            syncUsersList(conNetworkUserDB, conLocalUserDB);
            conNetworkUserDB.close();
            LOGGER.log(Level.INFO,"Network connection closed");

        } else {
            isOnline(false);
        }
        createUsersList(conLocalUserDB);
        conLocalUserDB.close();
        LOGGER.log(Level.INFO,"Local database connection closed\n");

        Connection conNetworkOutletDB;
        try {
            conNetworkOutletDB = getConnection("network_database");
            LOGGER.log(Level.FINE,"Connection to network database established");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Connection to network database failed");
            conNetworkOutletDB = null;
        }

        Connection conLocalOutletDB = getConnection("local_database");

        if (conNetworkOutletDB != null) {
            LOGGER.log(Level.FINE,"Connection to local database established");
            isOnline(true);
            syncOutletsList(conNetworkOutletDB, conLocalOutletDB);
            conNetworkOutletDB.close();
            LOGGER.log(Level.INFO,"Network connection closed");
        } else {
            isOnline(false);
        }
        createOutletList(conLocalOutletDB);
        conLocalOutletDB.close();
        LOGGER.log(Level.INFO,"Local database connection closed\n");
        LOGGER.log(Level.FINE,"Synchronization completed...\n");
        outletChoiceBox.setItems(outletsObservableList);

        outletChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, outlet, t1)
                -> {
            currentOutlet = outletChoiceBox.getValue();
            LOGGER.log(Level.INFO,"Outlet: \"" + currentOutlet.getOutlet() + "\" id: " + currentOutlet.getOutletId() + " selected");
            acceptOutletChoice.setDisable(false);
        });
        usernameTextField.textProperty().addListener((observable, oldValue, newValue)
                -> loginMessageLabel.setText(""));
        passwordField.textProperty().addListener((observable, oldValue, newValue)
                -> loginMessageLabel.setText(""));

    }
    @FXML
    void escapeKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE)  {
            LOGGER.log(Level.INFO,"Escape key pressed. Logged out...");
            Stage stage = (Stage) usernameTextField.getScene().getWindow();
            stage.close();
        }
    }
    @FXML
    private void okButtonEnterKey(KeyEvent okEvent) throws IOException {
        if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable() && !currentUser.isAdministrator())  {
            enterToSellerWorkTable();
        } else if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable() && currentUser.isAdministrator()) {
            enterToAdministratorWorkTable();
        }
    }
    @FXML
    private void acceptOutletChoiceOnAction() throws IOException {
        if (!acceptOutletChoice.isDisable() && !currentUser.isAdministrator())  {
            enterToSellerWorkTable();
        } else if (!acceptOutletChoice.isDisable() && currentUser.isAdministrator()) {
            enterToAdministratorWorkTable();
        }
    }
    @FXML
    void loginPasswordEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)  {
            validateActivator();
        }
    }
    public void cancelButtonOnAction() {
        LOGGER.log(Level.INFO,"Cancel button pressed. Logged out...");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonOnAction() {
        validateActivator();
    }
    /**
     * Данный метод производит валидацию пользователя по логину и паролю, <br>
     * а также создаёт объект текущего пользователя.
     */
    private void validateLogin() {
        String login = usernameTextField.getText();
        String password = passwordField.getText();
        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                currentUser = new User(user.getUserId()
                        , user.getLogin()
                        , user.getPassword()
                        , user.getFirstName()
                        , user.getLastName()
                        , user.getPhone()
                        , user.isAdministrator()
                        ,user.isActiveStuff());
                usernameTextField.setDisable(true);
                passwordField.setDisable(true);
                outletChoicePane.setVisible(true);
                break;
            }
        }
        if (!acceptOutletChoice.isVisible()) {
            loginMessageLabel.setText("Не правильный логин или имя пользователя");
        }
    }
    private void enterToAdministratorWorkTable() throws IOException {
        System.out.println("You are administrator");
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Stage stageSeller = new Stage();
        LOGGER.log(Level.INFO,"Start Administrator Controller...");
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("administrator.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageSeller.initStyle(StageStyle.UNDECORATED);
        stageSeller.setScene(scene);
        stageSeller.setTitle("CoffeeApp Administrator");
        stageSeller.setMaximized(true);

        stageSeller.show();
        stage.close();
        LOGGER.log(Level.INFO,"Login Controller closed\n");
    }
    private void enterToSellerWorkTable() throws IOException {
        users.clear();
        outlets.clear();

        Stage stage = (Stage) loginButton.getScene().getWindow();
        Stage stageSeller = new Stage();

        LOGGER.log(Level.INFO,"Start Seller Controller...");
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("seller.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageSeller.initStyle(StageStyle.UNDECORATED);
        stageSeller.setScene(scene);
        stageSeller.setTitle("CoffeeApp");
        stageSeller.setMaximized(true);

        stageSeller.show();
        stage.close();
        LOGGER.log(Level.INFO,"Login Controller closed\n");
    }
    private void isOnline(boolean status) {
        if (status) {
            networkIndicatorLabel.setText("в сети");
            networkIndicator.setFill(Color.GREEN);
        } else {
            networkIndicatorLabel.setText("не в сети");
            networkIndicator.setFill(Color.YELLOW);
        }
    }
    private void validateActivator() {
        if(!usernameTextField.getText().isBlank()
                && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Введите имя пользователя и пароль");
        }
    }
}