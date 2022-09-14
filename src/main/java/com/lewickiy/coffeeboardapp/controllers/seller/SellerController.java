package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import com.lewickiy.coffeeboardapp.UniqueIdGenerator;
import com.lewickiy.coffeeboardapp.controllers.login.LoginController;
import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList;
import com.lewickiy.coffeeboardapp.database.outlet.Outlet;
import com.lewickiy.coffeeboardapp.database.product.Product;
import com.lewickiy.coffeeboardapp.database.product.ProductList;
import com.lewickiy.coffeeboardapp.database.user.UserList;
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
    @FXML
    Button[] productButtons = new Button[20]; //массив кнопок продуктов
    Button[] numberButtons = new Button[10]; //массив цифровых кнопок

    /*____________________________________________________________________________________
     * @see ObservableList Изучить подробнее.
     * К этому моменту ArrayList с объектами продуктов уже сформирован. Он уже участвовал в именовании кнопок с продукцией.
     * Теперь он используется для создания ObservableList, который работает с TableView.
     * Все изменения по Продуктам происходят в currentSaleProducts. ObservableList нужен только для
     * отображения данных в saleTable.
     ____________________________________________________________________________________*/
    static ObservableList<SaleProduct> saleProductsObservableList = FXCollections.observableList(SaleProductList.currentSaleProducts);
    /*____________________________________________________________________________________
     * Кнопка закрытия смены.
     * После нажатия на неё должна закрываться смена текущего продавца.
     * Логика пока не установлена.
     ____________________________________________________________________________________*/
    @FXML
    private Button closeShiftButton; //кнопка закрытия смены

    /*____________________________________________________________________________________
     * Таблица позиций текущей продажи. Таблица включает в себя столбцы:
     * Наименование продукта;
     * Стоимость продукта;
     * Количество продукта;
     * Сумма исходя из количества.
     * Скидка, полагаю, будет находиться ниже таблицы, одной суммой.
     * На данный момент редактирование позиций нажатием на ячейку не представляется возможным
     * TODO редактирование ячеек нажатием на них. 06.09.2022
     ____________________________________________________________________________________*/
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

    /* ____________________________________________________________________________________
     * Цифровые кнопки количества позиций и отмены позиции.
     * Отмена позиции находится в этом ряду потому что, покупатель выбирает продукт, далее -
     * продавец должен нажать количество, но если покупатель передумывает, внимание продавца
     * сфокусировано на строке с цифрами количества, ему проще нажать кнопку отмены в этом ряду
     * (предположение).
     * TODO переименовать "Х" в 0.
     * TODO реализовать такой же принцип считывания значений, как в кнопках продукта.
     ____________________________________________________________________________________*/

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
    private Button zeroButton; //Пока не работает

    /*____________________________________________________________________________________
     * Кнопки продуктов. Здесь пока всё сложно. Кнопки одинаковые, несут один и тот же функционал,
     * только вписываются в них разные заголовки и идентификаторы товара.
     * Очень сложная система создания кнопок и их инициализации. Она упрощена массивом объектов Button,
     * но это так или иначе очень громоздко.
     * TODO найти решение упрощения системы создания кнопок с продуктами. Наверняка можно сделать это без вписывания их в FXML
     * TODO добавить ещё кнопки с продуктами.
     ____________________________________________________________________________________*/
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

    /*____________________________________________________________________________________
     * Блок текущей (выбранной) позиции. В него входят:
     * Изображение продукта или его символическое изображение;
     * Символ количества "Х";
     * Цифровое отображение количества единиц продукта;
     * Наименование позиции;
     * Кнопка Продажа;
     * Кнопка Скидка;
     * Кнопка Отмена.
     ____________________________________________________________________________________*/
    @FXML
    private ImageView productCategoryIco; //Графическое отображение товара
    @FXML
    private Label xLabel; //Символ X количество товара
    @FXML
    private Label amountLabel; //Цифровое отображение количества товара
    @FXML
    private Label sumLabel; //Сумма стоимости товара

    @FXML
    private Label productNameLabel; //наименование продукта
    @FXML
    private Button addProduct; //кнопка добавления продукта в текущий чек
    @FXML
    private Button discountButton; //кнопка скидки (в настоящее время не действует)
    @FXML
    private Button delProduct; //"С" - удалить продукт
    @FXML
    private Button endThisTale; //сформировать чек.
    @FXML
    private Button endThisTaleAnother; //отменить чек.
    /*____________________________________________________________________________________
    * Данный метод определяет действие при нажатии на кнопку product. <br>
    * В случае, если значение переменной newSale типа boolean == true, создаётся новая продажа, в неё <br>
    * добавляется текущий продукт. <br>
    * В конце процесса создания новой продажи значение newSale меняется на false. <br>
    * Теперь, последующие нажатия на кнопку product будут добавлять позиции в текущую продажу.
    ____________________________________________________________________________________*/

    @FXML
    void productOnAction(ActionEvent event) throws SQLException {
        Button button = (Button) event.getSource();
        if (newSale == true) {
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
                    productButtonsDisable(); //Спрятать кнопки продукта.
                    numberButtonsEnable(); //Показать цифровые кнопки.
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
                    productButtonsDisable(); //Спрятать кнопки продукта.
                    numberButtonsEnable(); //Показать цифровые кнопки.
                    productCategoryIco.setVisible(true); //Иконка продукта отображается (пока без логики).
                    productNameLabel.setText(product.getProduct()); //Рядом с иконкой продукта отображается наименование продукта.
                    currentProduct = new SaleProduct(product.getProductId(), product.getProduct(), product.getPrice());
                    break;
                } else {
                    //TODO в случае если произошла какая-то ошибка и продукт в кнопке не соответствует ни одному продукту в ArrayList products
                }
            }
        }
        xLabel.setVisible(true);
        productNameLabel.setVisible(true);
        addProduct.setVisible(true);
        discountButton.setVisible(true);
    }
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
        currentProduct.setAmountProdSale(Integer.parseInt(button.getAccessibleText())); //для currentProduct устанавливается количество продукта.
        currentProduct.setSumProdSale(currentProduct.getPriceProdSale() * currentProduct.getAmountProdSale()); //сумма стоимости продукта исходя из выбранного количества.
        numberButtonsDisable();
        productOperationButtonsEnable();
    }
    @FXML
    void delProductOnAction() {
        //TODO кнопка отмены ButtonOnAction
    }
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
        productOperationButtonsDisable();
        productButtonsEnabled(); //Кнопки с Продуктами становятся активными.
    }
    @FXML
    void zeroButtonOnAction(Event event) {
        currentProduct = null; //Текущий продукт становится null.
        productCategoryIco.setVisible(false); //Картинка Продукта перестаёт быть видимой
        xLabel.setVisible(false); //Символ количества перестаёт отображаться
        productNameLabel.setVisible(false); //Название продукта перестаёт отображаться
        amountLabel.setVisible(false); //Количество продукта перестаёт отображаться
        addProduct.setDisable(true); //Кнопка добавления продукта становится неактивной
        productOperationButtonsDisable(); //Кнопки с операциями по текущему продукту становятся недоступными.
        numberButtonsDisable(); //Цифровые кнопки становятся недоступными.
        productButtonsEnabled(); //Кнопки с Продуктами становятся активными.
    }
    /**
     * Данный метод реализует действие при нажатии кнопки "Закрыть смену". В настоящее время (27.08.2022) данный метод <br>
     * просто закрывает текущий рабочий стол продавца и снова открывает форму входа в систему <br>
     * @see LoginController <br>
     * TODO это должен быть метод, которому передаётся кнопка, т.к. данный код используется два раза и будет использоваться ещё в нескольких местах.
     */
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
    /**
     * TODO заполнить комментарий для документации.
     * Данный метод производит инициализацию кнопок продукции
     * Создаёт таблицу с позициями продажи
     */
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
        productOperationButtonsDisable();
        numberButtonsDisable();
        endThisTale.setDisable(true);
        endThisTaleAnother.setDisable(true);
        /*____________________________________________________________________________________
         * Здесь вызывается метод инициализации кнопок с Продуктами.
         ____________________________________________________________________________________*/
        initializationButtonProduct();
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
    public void initializationButtonProduct() { //Метод инициализации кнопок продуктов.
        productButtons = new Button[20];
        /**
         * Здесь проходит инициализация кнопок
         */
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
    public void productButtonsDisable() {
        for (Button buttonP : productButtons) { //Если Продукт есть и всё нормально, делаем недоступными кнопки с Продуктами.
            buttonP.setDisable(true); //теперь кнопки должны быть скрытыми пока я не выберу количество или не отменю позицию Продукта
        }
    }
    public void productButtonsEnabled() {
        for (Button buttonP : productButtons) { //Если Продукт есть и всё нормально, делаем недоступными кнопки с Продуктами.
            buttonP.setDisable(false); //теперь кнопки должны быть скрытыми пока я не выберу количество или не отменю позицию Продукта
        }
    }
    public void numberButtonsDisable() {
        for (Button buttonN : numberButtons) {
            buttonN.setDisable(true); //кнопки с номерами недоступны пока не выбран продукт
        }
    }
    public void numberButtonsEnable() {
        for (Button buttonN : numberButtons) {
            buttonN.setDisable(false); //кнопки с номерами становятся доступны когда продукт выбран
        }
    }
    public void productOperationButtonsDisable() {
        addProduct.setDisable(true);
        discountButton.setDisable(true);
        delProduct.setDisable(true);
    }
    public void productOperationButtonsEnable() {
        addProduct.setDisable(false);
        discountButton.setDisable(false);
        delProduct.setDisable(false);
    }
}
