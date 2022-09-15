package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList;
import com.lewickiy.coffeeboardapp.database.outlet.Outlet;
import com.lewickiy.coffeeboardapp.database.product.Product;
import com.lewickiy.coffeeboardapp.database.product.ProductList;
import com.lewickiy.coffeeboardapp.database.user.UserList;
import com.lewickiy.coffeeboardapp.idgenerator.UniqueIdGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;

import static com.lewickiy.coffeeboardapp.controllers.seller.ProductNameButton.productNameButton;
import static com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale.createNewSale;

public class SellerController {
    private boolean newSale = true; //boolean значение необходимости создания нового чека
    private int saleId; //Идентификатор текущей продажи. Создаётся в классе UniqueIdGenerator
    private int positionsCount;
    private CurrentSale currentSale; //объект - текущая продажа.
    private SaleProduct currentProduct; //объект - зона сбора данных.
    static ObservableList<SaleProduct> saleProductsObservableList = FXCollections.observableList(SaleProductList.currentSaleProducts);


    /*____________________________________start___________________________________________
     * Панель информации в верхней части экрана.
     * Здесь присутствуют кнопки:
     * Закрытие смены -
     * ...
     * А также метод логики нажатия на кнопку Закрытия смены
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private Button closeShiftButton; //кнопка закрытия смены

    //Действие при нажатии на кнопку Закрытия смены.
    @FXML
    void closeShiftButtonOnAction() throws IOException {
        Stage stage = (Stage) closeShiftButton.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("login.fxml"));
        Stage stageLogin = new Stage();
        Scene sceneLogin = new Scene(fxmlLoader.load());
        stageLogin.initStyle(StageStyle.UNDECORATED);
        stageLogin.setTitle("CoffeeApp");
        stageLogin.setScene(sceneLogin);
        stageLogin.show();
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель кнопок продуктов
     * Здесь присутствуют кнопки продуктов
     * Кнопки одинаковые, несут один и тот же функционал,
     * только вписываются в них разные заголовки и идентификаторы товара.
     * Очень сложная система создания кнопок и их инициализации. Она упрощена массивом объектов Button,
     * но это так или иначе очень громоздко.
     * Также здесь прописана логика при нажатии на кнопку с Продуктом.
     * TODO найти решение упрощения системы создания кнопок с продуктами. Наверняка можно сделать это без вписывания их в FXML
     * TODO добавить ещё кнопки с продуктами.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    Button[] productButtons = new Button[20]; //массив кнопок продуктов
    @FXML
    private Button product1;
    @FXML
    private Button product2;
    @FXML
    private Button product3;
    @FXML
    private Button product4;
    @FXML
    private Button product5;
    @FXML
    private Button product6;
    @FXML
    private Button product7;
    @FXML
    private Button product8;
    @FXML
    private Button product9;
    @FXML
    private Button product10;
    @FXML
    private Button product11;
    @FXML
    private Button product12;
    @FXML
    private Button product13;
    @FXML
    private Button product14;
    @FXML
    private Button product15;
    @FXML
    private Button product16;
    @FXML
    private Button product17;
    @FXML
    private Button product18;
    @FXML
    private Button product19;
    @FXML
    private Button product20;

    /**___________________________________________________________________________________
    * Данный метод определяет действие при нажатии на кнопку product. <br>
    * В случае, если значение переменной newSale типа boolean == true, создаётся новая продажа, в неё <br>
    * добавляется текущий продукт. <br>
    * В конце процесса создания новой продажи значение newSale меняется на false. <br>
    * Теперь, последующие нажатия на кнопку product будут добавлять позиции в текущую продажу.
    ____________________________________________________________________________________*/
    @FXML
    void productOnAction(ActionEvent event) throws SQLException {
        Button button = (Button) event.getSource();
        endThisTale.setDisable(false); //Кнопка Чек становится доступна
        System.out.println(button.getAccessibleText());
        if (newSale) {
            saleId = UniqueIdGenerator.getId(); //получаем новый уникальный идентификатор продажи.
            long nowDate = System.currentTimeMillis(); //Дата сейчас.
            Date saleDate = new Date(nowDate);
            long nowTime = System.currentTimeMillis(); //Время сейчас.
            Time saleTime = new Time(nowTime);
            currentSale = new CurrentSale(saleId, UserList.currentUser.getUserId(), Outlet.currentOutlet.getOutletId(), saleDate, saleTime); //Создаётся текущая продажа в буфере.
            createNewSale(currentSale); //Создаётся новая продажа в базе из currentSale.
            newSale = false; //последующие действия уже не должны создавать новую продажу.

            int idProductButton = Integer.parseInt(button.getAccessibleText()); //Записывается id нажатой кнопки (Продукт)
            // Здесь мы будем вставлять позицию в SaleProductList ПРИ СОЗДАНИИ НОВОГО ЧЕКА. Пока без загрузки в базу.
            for (Product product : ProductList.products) { //Циклом перебираются все продукты из products ArrayList
                if (product.getProductId() == idProductButton) { //Если id продукта соответствует id нажатой кнопки продукта,
                    productButtonsIsDisable(true); //Спрятать кнопки продукта.
                    numberButtonsIsDisable(false); //Показать цифровые кнопки.
                    productCategoryIco.setVisible(true); //Иконка продукта отображается (пока без логики).
                    productNameLabel.setText(product.getProduct()); //Рядом с иконкой продукта отображается наименование продукта.
                    currentProduct = new SaleProduct(product.getProductId(), product.getProduct(), product.getPrice());
                    //Добавляем данные в currentProduct
                    break;
                }
            }
        } else {
            int idProductButton = Integer.parseInt(button.getAccessibleText());
            for (Product product : ProductList.products) { //Циклом перебираются все продукты из products ArrayList
                if (product.getProductId() == idProductButton) { //Если id продукта соответствует id нажатой кнопки продукта,
                    productButtonsIsDisable(true); //Спрятать кнопки продукта.
                    numberButtonsIsDisable(false); //Показать цифровые кнопки.
                    productCategoryIco.setVisible(true); //Иконка продукта отображается (пока без логики).
                    productNameLabel.setText(product.getProduct()); //Рядом с иконкой продукта отображается наименование продукта.
                    currentProduct = new SaleProduct(product.getProductId(), product.getProduct(), product.getPrice());
                    break;
                } else {
                    System.out.println("Информационное сообщение: Кнопка Продукта не соответствует ни одному Продукту в базе данных.");
                    System.out.println("Пожалуйста, проверьте соединение и обратитесь в службу поддержки");
                    //TODO в случае если произошла какая-то ошибка и продукт в кнопке не соответствует ни одному продукту в ArrayList products
                }
            }
        }
        xLabel.setVisible(true);
        productNameLabel.setVisible(true);
        addProduct.setVisible(true);
        discountButtonActivate.setVisible(true);
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель цифровых кнопок
     * Набор кнопок от 0 до 9 для использования при выборе количества продуктов.
     * Также содержит логику при нажатии на Цифровую кнопку.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    Button[] numberButtons = new Button[10]; //массив цифровых кнопок
    @FXML
    private Button oneButton;
    @FXML
    private Button twoButton;
    @FXML
    private Button threeButton;
    @FXML
    private Button fourButton;
    @FXML
    private Button fiveButton;
    @FXML
    private Button sixButton;
    @FXML
    private Button sevenButton;
    @FXML
    private Button eightButton;
    @FXML
    private Button nineButton;
    @FXML
    private Button zeroButton;
    /**
     * Действие при нажатии на цифровую кнопку.
     * Количество выбранного продукта устанавливается исходя из AccessibleText цифровой кнопки.
     * В amountProdSale currentProduct устанавливается значение количества Продукта
     * А также суммы исходя из стоимости Продукта.
     * После чего цифровые кнопки становятся недоступными, а кнопки с дальнейшими операциями - доступными.
     * @param event - получает кнопку, которая сейчас была нажата для дальнейшей работы с ней.
     */
    @FXML
    void numberButtonOnAction(ActionEvent event) {
        Button button = (Button) event.getSource();
        amountLabel.setText(button.getAccessibleText()); //Label количества берёт данные из AccessibleText цифровой кнопки.
        amountLabel.setVisible(true);
        currentProduct.setAmount(Integer.parseInt(button.getAccessibleText())); //для currentProduct устанавливается количество продукта.
        currentProduct.setSum(currentProduct.getPrice() * currentProduct.getAmount()); //сумма стоимости продукта исходя из выбранного количества.
        numberButtonsIsDisable(true);
        productOperationButtonsIsDisable(false);
    }

    /**
     * Действие при нажатии на кнопку "0" в панели Цифровых кнопок.<br>
     * Нажатие на эту кнопку отличается от нажатия на прочие Цифровые кнопки.<br>
     * При нажатии на кнопку "0" происходит присвоение null для объекта currentProduct.<br>
     * После этого становятся активны кнопки с выбором Продукта (мы возвращаемся в изначальное состояние Продажи).<br>
     * @param event - ... .
     */
    @FXML
    void zeroButtonOnAction(Event event) {
        currentProduct = null; //Текущий продукт становится null.
        productCategoryIco.setVisible(false); //Картинка Продукта перестаёт быть видимой
        xLabel.setVisible(false); //Символ количества перестаёт отображаться
        productNameLabel.setVisible(false); //Название продукта перестаёт отображаться
        amountLabel.setVisible(false); //Количество продукта перестаёт отображаться
        addProduct.setDisable(true); //Кнопка добавления продукта становится неактивной
        productOperationButtonsIsDisable(true); //Кнопки с операциями по текущему продукту становятся недоступными.
        numberButtonsIsDisable(true); //Цифровые кнопки становятся недоступными.
        productButtonsIsDisable(false); //Кнопки с Продуктами становятся активными.
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель таблицы текущего чека
     * Таблица содержит в себе несколько столбцов:
     * Продукт - здесь отображаются добавленные продукты
     * Цена - цена отражённая в прайсе
     * Количество - количество добавленного продукта.
     * На данный момент редактирование данных по нажатию на ячейку не реализовано
     * //TODO создать редактирование данных в таблице по нажатию на ячейку с данными
     * Также присутствует Label с отображением суммы заказа sumLabel
     * //TODO добавить отображение суммы чека со скидкой
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private TableView<SaleProduct> saleTable; //таблица продаж
    @FXML
    private TableColumn<SaleProduct, String> productColumn; //колонка с наименованием продукта
    @FXML
    private TableColumn<SaleProduct, Double> priceColumn; //колонка со стоимостью продукта
    @FXML
    private TableColumn<SaleProduct, Integer> amountColumn; //количество продукта
    @FXML
    private TableColumn<SaleProduct, Double> sumColumn; //сумма стоимости продукта исходя из количества
    @FXML
    private Label sumLabel; //Сумма стоимости товара
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель текущего товара.
     * Здесь происходят операции с товаром, который ещё не добавлен в текущий чек.
     * Изображение продукта или его символическое изображение;
     * Символ количества "Х";
     * Цифровое отображение количества единиц продукта;
     * Наименование позиции;
     * Кнопка Продажа;
     * Кнопка Скидка;
     * Кнопка Отмена.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private ImageView productCategoryIco; //Графическое отображение товара
    @FXML
    private Label xLabel; //Символ X количество товара
    @FXML
    private Label amountLabel; //Цифровое отображение количества товара
    @FXML
    private Label productNameLabel; //наименование продукта
    @FXML
    private Button addProduct; //кнопка добавления продукта в текущий чек (Зелёный плюсик)
    @FXML
    private Button discountButtonActivate; //Кнопка скидки (в настоящее время не действует). (Знак процента)
    @FXML
    private Button delProduct; //"С" - удалить продукт (действует так же как и 0???)
    @FXML
    private Button endThisTale; //сформировать чек.
    @FXML
    private Button endThisTaleAnother; //отменить чек.

    //Логика нажатия на кнопку со Скидкой
    @FXML
    void discountButtonActivateOnAction(ActionEvent event) {
        System.out.println("Делаем скидку");
        discountPanel.setVisible(true);
    }

    //На данный момент кнопка не имеет действия. По сути, просто зарезервированное место.
    @FXML
    void delProductOnAction() {
        //TODO кнопка отмены ButtonOnAction
    }
    //Логика при нажатии на кнопку "+" добавления продукта в текущий чек.
    @FXML
    void addProductOnAction() {
        SaleProductList.addProductToArray(positionsCount, currentProduct, saleId);
        positionsCount++;
        saleTable.setItems(saleProductsObservableList); //Установка значений в таблицу.
        saleTable.refresh(); //Обновление таблицы. Без этого отображается только первая строка.

        //Подсчёт суммы продажи под таблицей текущей продажи.
        double total = 0.0;
        for (SaleProduct saleProduct : saleTable.getItems()) {
            total = total + saleProduct.getSumProdSale();
        }
        sumLabel.setText(String.valueOf(total)); //Сумма устанавливается в sumLabel
        productCategoryIco.setVisible(false); //Картинка Продукта перестаёт быть видимой
        xLabel.setVisible(false); //Символ количества перестаёт отображаться
        productNameLabel.setVisible(false); //Название продукта перестаёт отображаться
        amountLabel.setVisible(false); //Количество продукта перестаёт отображаться
        addProduct.setDisable(true); //Кнопка добавления продукта становится неактивной
        productOperationButtonsIsDisable(true);
        productButtonsIsDisable(false); //Кнопки с Продуктами становятся активными.
    }
    @FXML
    void endThisTaleOnAction() throws SQLException {
        addProductsToSale(currentSaleProducts, currentSale);
        endThisTale.setDisable(true);
        newSale = true;
        currentSale = null;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.0");
        saleTable.refresh();
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель скидок на позицию.
     * Данная панель открывается поверх панели Текущего товара нажатием на кнопку "%" находящуюся в панели
     * текущего товара.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private AnchorPane discountPanel; //Непосредственно сама панель. В ней содержатся все актуальные скидки.
    @FXML
    private Button discount1;
    @FXML
    private Button discount2;
    @FXML
    private Button discount3;
    @FXML
    private Button discount4;
    @FXML
    private Button discount5;
    @FXML
    private Button discount6;
    @FXML
    private Button discount7;
    @FXML
    private Button discount8;
    @FXML
    private Button discount9;

    /**
     * Логика нажатия на кнопку "%".
     * @param event - принимается параметр ActionEvent
     */
    @FXML
    void discountOnAction(ActionEvent event) {
        //TODO
        System.out.println("discount button pressed");
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Инициализация
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    void initialize() {
        //Изменения в currentSaleProducts происходят также в saleProductsObservableList благодаря Listener.
        saleProductsObservableList.addListener(new ListChangeListener<SaleProduct>() {
            @Override
            public void onChanged(Change<? extends SaleProduct> change) {
            }
        });
        numberButtons[0] = zeroButton;
        numberButtons[1] = oneButton;
        numberButtons[2] = twoButton;
        numberButtons[3] = threeButton;
        numberButtons[4] = fourButton;
        numberButtons[5] = fiveButton;
        numberButtons[6] = sixButton;
        numberButtons[7] = sevenButton;
        numberButtons[8] = eightButton;
        numberButtons[9] = nineButton;
        for (int i = 0; i < numberButtons.length; i++) {
            numberButtons[i].setAccessibleText(String.valueOf(i));
        }
        /*____________________________________________________________________________________
         * Блок текущего Продукта при добавлении его в Текущую продажу
         * Изначально, до создания текущей продажи, данное графическое представление не отображается.
         * Отображение происходит при первом нажатии на кнопку Продукта.
         * После нажатия на кнопку (пока её нет) переноса Продукта в текущий чек блок возвращается
         * в изначальное состояние (не отображается).
         ____________________________________________________________________________________*/
        productCategoryIco.setVisible(false);
        xLabel.setVisible(false);
        amountLabel.setVisible(false);
        productNameLabel.setVisible(false);
        productOperationButtonsIsDisable(true);
        numberButtonsIsDisable(true);
        endThisTale.setDisable(true);
        endThisTaleAnother.setDisable(true);
        /*____________________________________________________________________________________
         * Здесь вызывается метод инициализации кнопок с Продуктами.
         ____________________________________________________________________________________*/
        initializationProductButton();
        /*____________________________________________________________________________________
         * Здесь происходит инициализация столбцов таблицы текущей продажи.
         * В неё добавляются позиции Продуктов.
         * setCellValueFactory определяет что добавляется и в какой столбец.
         ____________________________________________________________________________________*/
        productColumn.setCellValueFactory(new PropertyValueFactory<>("prodName"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("priceProdSale"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amountProdSale"));
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sumProdSale"));
    }

    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Прочие методы.
     _____________________________________˅˅˅____________________________________________*/
    public void initializationProductButton() { //Метод инициализации кнопок продуктов.
        productButtons = new Button[20];
        productButtons[0] = product1;
        productButtons[1] = product2;
        productButtons[2] = product3;
        productButtons[3] = product4;
        productButtons[4] = product5;
        productButtons[5] = product6;
        productButtons[6] = product7;
        productButtons[7] = product8;
        productButtons[8] = product9;
        productButtons[9] = product10;
        productButtons[10] = product11;
        productButtons[11] = product12;
        productButtons[12] = product13;
        productButtons[13] = product14;
        productButtons[14] = product15;
        productButtons[15] = product16;
        productButtons[16] = product17;
        productButtons[17] = product18;
        productButtons[18] = product19;
        productButtons[19] = product20;

        for (int i = 0; i < productButtons.length; i++) { //привязывание объектов кнопок происходит через цикл
            String initProducts = "fx:id=\"product" + (i + 1) + "\" was not injected: check your FXML file 'seller.fxml'.";
            assert productButtons[i] != null : initProducts;
        }
        productNameButton(productButtons);
    }

    /**
     * Данный метод делает кнопки с Продуктами активными/неактивными.
     * Например: при выборе Цифровыми кнопками количества, Кнопки с Продуктами неактивны.
     * @param res - типа boolean является переключателем для метода.
     */
    public void productButtonsIsDisable(boolean res) {
        for (Button buttonP : productButtons) { //Если Продукт есть и всё нормально, делаем недоступными кнопки с Продуктами.
            buttonP.setDisable(res); //теперь кнопки должны быть скрытыми пока я не выберу количество или не отменю позицию Продукта
        }
    }

    /**
     * Данный метод делает Цифровые кнопки активными/неактивными.
     * Например: Цифровые кнопки недоступны пока не выбран продукт.
     * @param res - тип boolean, который работает как переключатель
     */
    public void numberButtonsIsDisable(boolean res) {
        for (Button buttonN : numberButtons) {
            buttonN.setDisable(res);
        }
    }

    /**
     * Данный метод делает кнопки с действиями с Добавляемым продуктом активными/неактивными
     * Например: кнопки Работы с Выбранным продуктом недоступны пока не выбран продукт.
     * @param res - тип boolean, который работает как переключатель
     */
    public void productOperationButtonsIsDisable(boolean res) {
        addProduct.setDisable(res);
        discountButtonActivate.setDisable(res);
        delProduct.setDisable(res);
    }
}