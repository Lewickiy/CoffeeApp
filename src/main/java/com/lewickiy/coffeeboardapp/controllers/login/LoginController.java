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
    private Connection conNetworkUserDB = null;
    private Connection conLocalUserDB = null;
    private Connection conNetworkOutletDB = null;
    private Connection conLocalOutletDB = null;
    static ObservableList<Outlet> outletsObservableList = FXCollections.observableList(outlets);
    @FXML
    private AnchorPane outletChoicePane;
    @FXML
    private ChoiceBox<Outlet> outletChoiceBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button acceptOutletChoice;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Circle networkIndicator;
    @FXML
    private Label networkIndicatorLabel;

    @FXML
    void initialize() throws SQLException, ParseException {
        networkIndicator.setFill(Color.YELLOW);
        //Если удаётся установить соединение с сетевой базой данных, то подключаемся к ней и
        //проводим синхронизацию с ней локальной базы данных,
        //после чего загружаем список пользователей из локальной базы данных для продолжения работы.
        //Если соединение с сетевой базой данных не установлено, загружаем список пользователей из
        //локальной базы данных, пропуская её синхронизацию с сетевой.
        try {
            conNetworkUserDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx);
        }

        conLocalUserDB = getConnection("local_database");

        if (conNetworkUserDB != null) {
            isOnline(true);
            syncUsersList(conNetworkUserDB, conLocalUserDB);
            conNetworkUserDB.close();

        } else {
            isOnline(false);
        }
        createUsersList(conLocalUserDB);
        conLocalUserDB.close();

        try {
            conNetworkOutletDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            System.out.println(sqlEx);
        }

        conLocalOutletDB = getConnection("local_database");

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
        acceptOutletChoice.setDisable(true);
        /*
         *Это Listener для outletChoiceBox. Он следит за изменениями в выборе и создаёт из них объект
         * currentOutlet класса Outlet. Дальше я буду с ним работать для фильтрации администратором продаж
         * для дополнения приветствия пользователя и прочего.
         */
        outletChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, outlet, t1) -> {
            currentOutlet = outletChoiceBox.getValue();
            acceptOutletChoice.setDisable(false);
        });
        usernameTextField.textProperty().addListener((observable, oldValue, newValue) -> loginMessageLabel.setText(""));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> loginMessageLabel.setText(""));

        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'login.fxml'.";
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'Untitled'.";
        assert loginMessageLabel != null : "fx:id=\"loginMessageLabel\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordField != null : "fx:id=\"passwordField\" was not injected: check your FXML file 'Untitled'.";
        assert usernameTextField != null : "fx:id=\"usernameTextField\" was not injected: check your FXML file 'Untitled'.";
    }
    /**
     *  При нажатии клавиши escape, когда текстовый курсор установлен в поле "Логин" <br>
     *  (по умолчанию при запуске окна верификации), происходит выход из сцены.
     */
    @FXML
    void escapeKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE)  {
            Stage stage = (Stage) usernameTextField.getScene().getWindow();
            stage.close();
        }
    }
    @FXML
    private void okButtonEnterKey(KeyEvent okEvent) throws IOException {
        if (okEvent.getCode() == KeyCode.ENTER && acceptOutletChoice.isDisable() == false)  {
            acceptOutletChoice.setDisable(true);
            enterToSellerWorkTable();
        }
    }
    @FXML
    private void acceptOutletChoiceOnAction() throws IOException {
        if (currentOutlet != null) {
            enterToSellerWorkTable();
        }
    }
    /**
     * Метод выполняет вход в систему по нажатию клавиши Enter на клавиатуре пользователя.<br>
     */
    @FXML
    void loginPasswordEnterKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)  {
            if(!usernameTextField.getText().isBlank()
                    && !passwordField.getText().isBlank()) {
                validateLogin();
            } else {
                loginMessageLabel.setText("Введите имя пользователя и пароль");
            }
        }
    }
    /**
     * Действие, выполняемое при нажатии пользователем кнопки "Отмена". <br>
     * Происходит закрытие сцены. Программа завершает работу. <br>
     */
    public void cancelButtonOnAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    /**
     * Данный метод реализует действие при нажатии на кнопку "Войти". <br>
     * Если поля с логином и паролем не пустые, происходит запуск метода validateLogin();<br>
     * В противном случае пользователь через loginMessageLabel получает сообщение<br>
     * о необходимости ввода имени пользователя и пароля.<br>
     */
    public void loginButtonOnAction() { //Login Button Action
        if(!usernameTextField.getText().isBlank()
                && !passwordField.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Введите имя пользователя и пароль");
        }
    }
    /**
     * Данный метод производит валидацию пользователя по логину и паролю. <br>
     */
    private void validateLogin() {
        String login = usernameTextField.getText();
        String password = passwordField.getText();
        for (User user : users) {
            if (user.getLogin().equals(login) && user.getPassword().equals(password)) {
                currentUser.setUserId(user.getUserId());
                currentUser.setLogin(user.getLogin());
                currentUser.setPassword(user.getPassword());
                currentUser.setFirstName(user.getFirstName());
                currentUser.setLastName(user.getLastName());
                currentUser.setPhone(user.getPhone());
                currentUser.setAdministrator(user.isAdministrator());
                currentUser.setActiveStuff(user.isActiveStuff());
                usernameTextField.setDisable(true);
                passwordField.setDisable(true);

                outletChoicePane.setVisible(true);
                System.out.println(outletChoiceBox.isFocused());
                outletChoiceBox.setFocusTraversable(true);
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
        //Это уходит в кнопку Ok после выбора Торговой точки.
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Stage stageSeller = new Stage(); //запуск второй сцены
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("seller.fxml")); //запуск seller.fxml
        Scene scene = new Scene(fxmlLoader.load());
        stageSeller.initStyle(StageStyle.UNDECORATED); //Сцена с оформлением, кнопками свернуть, развернуть, закрыть, титульным заголовком.
        stageSeller.setScene(scene);
        stageSeller.setTitle("CoffeeApp. Добро пожаловать, " + currentUser.getFirstName());
        stageSeller.setMaximized(true); //сцена при запуске развёрнута на весь экран

        stageSeller.show();
        stage.close();
    }
    private void isOnline(boolean status) {
        if (status == true) {
            networkIndicatorLabel.setText("в сети");
            networkIndicator.setFill(Color.GREEN);
        } else {
            networkIndicatorLabel.setText("не в сети");
            networkIndicator.setFill(Color.YELLOW);
        }
    }
}