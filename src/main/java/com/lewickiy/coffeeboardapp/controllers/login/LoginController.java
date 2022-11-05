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
        Connection conNetworkUserDB;
        try {
            conNetworkUserDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            conNetworkUserDB = null;
        }

        Connection conLocalUserDB = getConnection("local_database");

        if (conNetworkUserDB != null) {
            isOnline(true);
            syncUsersList(conNetworkUserDB, conLocalUserDB);
            conNetworkUserDB.close();

        } else {
            isOnline(false);
        }
        createUsersList(conLocalUserDB);
        conLocalUserDB.close();

        Connection conNetworkOutletDB;
        try {
            conNetworkOutletDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            conNetworkOutletDB = null;
        }

        Connection conLocalOutletDB = getConnection("local_database");

        if (conNetworkOutletDB != null) {
            isOnline(true);
            syncOutletsList(conNetworkOutletDB, conLocalOutletDB);
            conNetworkOutletDB.close();
        } else {
            isOnline(false);
        }
        createOutletList(conLocalOutletDB);
        conLocalOutletDB.close();
        outletChoiceBox.setItems(outletsObservableList);

        /* Это Listener для outletChoiceBox. Он следит за изменениями в выборе и создаёт
        из них объект currentOutlet класса Outlet.*/
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
    private void okButtonEnterKey(KeyEvent okEvent) throws IOException {
        if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable())  {
            enterToSellerWorkTable();
        }
    }
    @FXML
    private void acceptOutletChoiceOnAction() throws IOException {
        if (currentOutlet != null) {
            enterToSellerWorkTable();
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
    private void enterToSellerWorkTable() throws IOException {
        users.clear();
        outlets.clear();

        Stage stage = (Stage) loginButton.getScene().getWindow();
        Stage stageSeller = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("seller.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stageSeller.initStyle(StageStyle.UNDECORATED);
        stageSeller.setScene(scene);
        stageSeller.setTitle("CoffeeApp");
        stageSeller.setMaximized(true);

        stageSeller.show();
        stage.close();
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