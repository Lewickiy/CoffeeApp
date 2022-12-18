package com.lewickiy.coffeeboardapp.controllers.login;

import com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.dao.connector.Database;
import com.lewickiy.coffeeboardapp.entities.outlet.Outlet;
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
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.syncOutletsList;
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.syncUsersList;
import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.createOutletList;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.outlets;
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

        Connection conNDB;
        Connection conLDB = getConnection(Database.LOCAL_DB);
        try {
            LOGGER.log(Level.INFO,"Start network database synchronization on Login startup");
            conNDB = getConnection(Database.NETWORK_DB);
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncUsersList(conNDB, conLDB);
            syncOutletsList(conNDB, conLDB);
            createUsersList(conLDB);
            createOutletList(conLDB);
            // При появлении синхронизации каких-либо дополнительных данных. Метод вносить сюда и ниже*.
            conNDB.close();
            LOGGER.log(Level.FINE, "Synchronization completed successfully");
        } catch (SQLException sqlException) {
            LOGGER.log(Level.WARNING,"When starting to synchronize with the network database at Login startup, it was not possible to connect to the network. Data is taken from local database");
            isOnline(networkIndicatorLabel, networkIndicator,false);
            createUsersList(conLDB);
            createOutletList(conLDB);
            //*
            LOGGER.log(Level.INFO, "Further work may be carried out with outdated data");
        } finally {
            conLDB.close();
        }

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
        if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable())  {
            enterToWorkTable(WorkTableChoice.SELLER, loginButton);
        }
    }
    @FXML
    private void acceptOutletChoiceOnAction() {
        if (!acceptOutletChoice.isDisable())  {
            enterToWorkTable(WorkTableChoice.SELLER, loginButton);
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
    private void validateLogin(Button button) {
        for (User user : users) {
            if (user.getLogin().equals(usernameTextField.getText()) && user.getPassword().equals(passwordField.getText())) {
                currentUser = new User(user.getUserId()
                        , user.getLogin()
                        , user.getPassword()
                        , user.getFirstName()
                        , user.getLastName()
                        , user.getPhone()
                        , user.isAdministrator()
                        , user.isActiveStuff());
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
            validateLogin(loginButton);
        } else {
            loginMessageLabel.setText("Введите имя пользователя и пароль");
        }
    }
}