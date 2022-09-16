package com.lewickiy.coffeeboardapp.controllers.login;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import com.lewickiy.coffeeboardapp.database.outlet.Outlet;
import com.lewickiy.coffeeboardapp.database.user.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.createOutletList;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.createPaymentTypeAL;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.createProductsList;
import static com.lewickiy.coffeeboardapp.database.user.UserList.*;

public class LoginController {
    static ObservableList<Outlet> outletsObservableList = FXCollections.observableList(outlets);
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Label usernameLabel;
    @FXML
    private ChoiceBox<Outlet> outletChoiceBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button acceptOutletChoice;
    @FXML
    private Label passwordLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Label choiceLabel;
    @FXML
    void initialize() throws SQLException {
        acceptOutletChoice.setDisable(true);
        createUsersList(); //Загрузка объектов из базы в список пользователей.
        createProductsList(); //Загрузка объектов из базы в список продуктов.
        createOutletList(); //Загрузка объектов из базы в список торговых точек.
        createDiscountList();
        createPaymentTypeAL();
        outletChoiceBox.setItems(outletsObservableList); //Устанавливаем значения в ChoiceBox из observableList
        outletChoiceBox.setAccessibleText("Выберете рабочее место"); //Выбор отмеченной по умолчанию позиции из торговых точек
        /*
         *Это Listener для outletChoiceBox. Он следит за изменениями в выборе и создаёт из них объект
         * currentOutlet класса Outlet. Дальше я буду с ним работать для фильтрации администратором продаж
         * для дополнения приветствия пользователя и прочего.
         */
        outletChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Outlet>() {
            @Override
            public void changed(ObservableValue<? extends Outlet> observableValue, Outlet outlet, Outlet t1) {
                currentOutlet = outletChoiceBox.getValue();
                System.out.println(currentOutlet.getOutletId() + " " + currentOutlet.getOutlet());
                acceptOutletChoice.setDisable(false);
            }
        });
        //_____________________________________________________________________________________________



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
    private void acceptOutletChoiceOnAction (ActionEvent event) throws IOException {
        if (currentOutlet == null) {
            System.out.println("Проверьте подключение к интернету и перезапустите систему, или свяжитесь с службой поддержки");
        } else {
            //Это уходит в кнопку Ok после выбора Торговой точки.
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close(); //закрытие первой сцены
            Stage stageSeller = new Stage(); //запуск второй сцены
            FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("seller.fxml")); //запуск seller.fxml
            Scene scene = new Scene(fxmlLoader.load());
            stageSeller.initStyle(StageStyle.DECORATED); //Сцена с оформлением, кнопками свернуть, развернуть, закрыть, титульным заголовком.
            stageSeller.setScene(scene);
            stageSeller.setTitle("CoffeeApp. Добро пожаловать, " + currentUser.getFirstName());
            stageSeller.setMaximized(true); //сцена при запуске развёрнута на весь экран
            //TODO проверку на наличие организационной структуры My SQL базы. Если это первый запуск, вся структура должна создаваться
//      sqlCheckTable(); //проверяется наличие таблицы sale. Это надо сделать ранее. При запуске программы.
            stageSeller.show();
        }
    }
    /**
     * Метод выполняет вход в систему по нажатию клавиши Enter на клавиатуре пользователя.<br>
     * TODO перенести код входа в отдельный метод чтобы не дублировать его при нажатии на клавишу и нажатии на кнопку "Войти"
     */
    @FXML
    void loginPasswordEnterKey(KeyEvent event) throws IOException {
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
     * @param event - IDE пишет что параметр не используется.
     */
    public void cancelButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    /**
     * Данный метод реализует действие при нажатии на кнопку "Войти". <br>
     * Если поля с логином и паролем не пустые, происходит запуск метода validateLogin();<br>
     * В противном случае пользователь через loginMessageLabel получает сообщение<br>
     * о необходимости ввода имени пользователя и пароля.<br>
     * @param event - не используется, но должен быть. Что здесь я не понимаю?
     */
    public void loginButtonOnAction(ActionEvent event) throws IOException { //Login Button Action
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
    private void validateLogin() throws IOException {
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

                //Меняются блоки отображения
                usernameLabel.setVisible(false); //становится невидимой
                usernameTextField.setVisible(false); //становится невидимой

                passwordLabel.setVisible(false); //становится невидимой
                passwordField.setVisible(false); //становится невидимой

                loginButton.setVisible(false); //становится невидимой
                cancelButton.setVisible(false); //становится невидимой

                choiceLabel.setVisible(true);
                outletChoiceBox.setVisible(true);
                acceptOutletChoice.setVisible(true);
                break; //И всё?! Просто break ?!
            } else {
                loginMessageLabel.setText("Не правильный логин или имя пользователя"); //TODO В интерфейсе пользователя нужно сделать TextLabel выше чтобы не вмещающийся текст влез, может с переносом строки
            }
        }
    }
}