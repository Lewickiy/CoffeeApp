package com.lewickiy.coffeeboardapp.controllers.login;

import com.lewickiy.coffeeboardapp.controllers.actions.worktable.WorkTableChoice;
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

import static com.lewickiy.coffeeboardapp.controllers.actions.worktable.WorkTable.enterToWorkTable;
import static com.lewickiy.coffeeboardapp.dao.connector.NetworkIndicator.isOnline;
import static com.lewickiy.coffeeboardapp.dao.sync.StartData.syncStartData;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.currentUser;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.users;


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
    void initialize() {
        isOnline(networkIndicatorLabel, networkIndicator);
        syncStartData();

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
    private void acceptOutletChoiceOnAction() {
        if (!outletChoicePane.isVisible())  {
            enterToWorkTable(WorkTableChoice.SELLER, loginButton);
        }
    }
    @FXML
    private void okButtonEnterKey(KeyEvent okEvent) {
        if (okEvent.getCode() == KeyCode.ENTER && !acceptOutletChoice.isDisable())  {
            enterToWorkTable(WorkTableChoice.SELLER, loginButton);
        }
    }
    @FXML
    public void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void loginButtonOnAction() {
        validateActivator();
    }
    @FXML
    void loginPasswordEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            validateActivator();
        }
    }
    private void validateLogin() {
        for (User user : users) {
            if (user.getLogin().equals(usernameTextField.getText()) && user.getPassword().equals(passwordField.getText())) {
                currentUser = new User(user.getId()
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
        if (!outletChoicePane.isVisible()) {
            loginMessageLabel.setText("Не правильный логин или имя пользователя");
        }
    }
    private void validateActivator() {
        if(!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Введите имя пользователя и пароль");
        }
    }
}