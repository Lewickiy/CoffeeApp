package com.lewickiy.coffeeboardapp.controllers.login;

import com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.database.outlet.Outlet;
import com.lewickiy.coffeeboardapp.entities.user.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTable.enterToWorkTable;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.NetworkIndicator.isOnline;
import static com.lewickiy.coffeeboardapp.database.connection.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.local.SyncLocalDB.syncOutletsList;
import static com.lewickiy.coffeeboardapp.database.local.SyncLocalDB.syncUsersList;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.createOutletList;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.*;

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
        Connection conNetworkUserDB;
        try {
            conNetworkUserDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Connection to network database failed");
            conNetworkUserDB = null;
        }

        Connection conLocalUserDB = getConnection("local_database");

        if (conNetworkUserDB != null) {
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncUsersList(conNetworkUserDB, conLocalUserDB);
            conNetworkUserDB.close();

        } else {
            isOnline(networkIndicatorLabel, networkIndicator,false);
        }
        createUsersList(conLocalUserDB);
        conLocalUserDB.close();

        Connection conNetworkOutletDB;
        try {
            conNetworkOutletDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.WARNING,"Connection to network database failed");
            conNetworkOutletDB = null;
        }

        Connection conLocalOutletDB = getConnection("local_database");

        if (conNetworkOutletDB != null) {
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncOutletsList(conNetworkOutletDB, conLocalOutletDB);
            conNetworkOutletDB.close();
        } else {
            isOnline(networkIndicatorLabel, networkIndicator,false);
        }
        createOutletList(conLocalOutletDB);
        conLocalOutletDB.close();
        outletChoiceBox.setItems(outletsObservableList);

        outletChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, outlet, t1)
                -> {
            currentOutlet = outletChoiceBox.getValue();
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
            Stage stage = (Stage) usernameTextField.getScene().getWindow();
            stage.close();
        }
    }
    @FXML
    private void okButtonEnterKey(KeyEvent okEvent) {
        if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable() && !currentUser.isAdministrator())  {
            enterToWorkTable(WorkTableChoice.SELLER, loginButton);
        } else if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable() && currentUser.isAdministrator()) {
            enterToWorkTable(WorkTableChoice.ADMINISTRATOR, loginButton);
        }
    }
    @FXML
    private void acceptOutletChoiceOnAction() {
        if (!acceptOutletChoice.isDisable() && !currentUser.isAdministrator())  {
            enterToWorkTable(WorkTableChoice.SELLER, loginButton);
        } else if (!acceptOutletChoice.isDisable() && currentUser.isAdministrator()) {
            enterToWorkTable(WorkTableChoice.ADMINISTRATOR, loginButton);
        }
    }
    @FXML
    void loginPasswordEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)  {
            validateActivator();
        }
    }
    public void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void loginButtonOnAction() {
        validateActivator();
    }
    /**
     * Данный метод производит валидацию пользователя по логину и паролю, <br>
     * а также создаёт объект текущего пользователя.
     */
    private void validateLogin() {
        for (User user : users) {
            if (user.getLogin().equals(usernameTextField.getText()) && user.getPassword().equals(passwordField.getText())) {
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
    private void validateActivator() {
        if(!usernameTextField.getText().isBlank()
                && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Введите имя пользователя и пароль");
        }
    }
}