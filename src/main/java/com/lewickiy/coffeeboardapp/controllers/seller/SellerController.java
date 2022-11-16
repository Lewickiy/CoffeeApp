package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList;
import com.lewickiy.coffeeboardapp.database.discount.Discount;
import com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySales;
import com.lewickiy.coffeeboardapp.database.paymentType.PaymentType;
import com.lewickiy.coffeeboardapp.database.product.Product;
import com.lewickiy.coffeeboardapp.database.product.ProductCategory;
import com.lewickiy.coffeeboardapp.database.user.UserList;
import com.lewickiy.coffeeboardapp.idgenerator.UniqueIdGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.seller.DiscountNameButton.discountNameButtons;
import static com.lewickiy.coffeeboardapp.controllers.seller.ProductNameButton.productNameButton;
import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.Query.*;
import static com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale.addSaleToLocalDB;
import static com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct.addSaleProductsToLocalDB;
import static com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.database.local.SyncLocalDB.*;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesList.*;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.createPaymentTypeList;
import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.createProductCategoriesList;
import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.createProductsList;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.database.query.CheckShift.checkShift;
import static com.lewickiy.coffeeboardapp.database.query.OpenCloseShift.updateShiftSql;
import static com.lewickiy.coffeeboardapp.database.query.ShiftLog.*;
import static com.lewickiy.coffeeboardapp.database.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.database.query.SyncSales.syncSales;

public class SellerController {

    //TODO ArrayLists зафиксировать изначальный размер
    //TODO Исключения залогировать и выдать пользователю user friendly сообщение.
    private boolean newSale = true; //boolean значение необходимости создания нового чека
    private int saleId;
    private boolean startSync = true;
    @FXML
    private Label timeLabel;
    private int positionsCount;
    private CurrentSale currentSale;
    private SaleProduct currentProduct;
    static ObservableList<SaleProduct> saleProductsObservableList = FXCollections.observableList(currentSaleProducts);
    static ObservableList<SaleProduct> todaySalesObservableList = FXCollections.observableList(todaySalesArrayList);
    @FXML
    private Circle networkIndicator;
    @FXML
    private Label networkIndicatorLabel;


    /*____________________________________start___________________________________________
     * Панель информации в верхней части экрана.
     * Здесь присутствуют кнопки:
     * Закрытие смены -
     * Отображение продаж
     * А также метод логики нажатия на кнопку Закрытия смены
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private Button closeShiftButton; //кнопка закрытия смены
    @FXML
    private Button openShiftButton;
    @FXML
    private Button allSales;
    @FXML
    private Label cashDepositLabel;
    @FXML
    private Label allCashLabel;
    @FXML
    private Label litresLabel;
    @FXML
    void allSalesOnAction() throws SQLException, ParseException {
        Connection con = getConnection("local_database");
        addAllSalesToArray(con);
        con.close();
        allSalesTable.setItems(todaySalesObservableList);
        allSalesTable.refresh();
        cashSumSaleLabel.setText(sumCash() + " руб.");
        cardSumSaleLabel.setText(sumCard() + " руб.");
        allSumSaleLabel.setText(sumAll() + " руб.");
        cashDepositLabel.setText(getCashDeposit() + " руб.");
        allCashLabel.setText((sumCash() + getCashDeposit()) + " руб.");
        if (allSalesPane.isVisible()) {
            todaySalesArrayList.clear();
        }
        litresLabel.setText(String.valueOf(litresSum()));
        allSalesPane.setVisible(!allSalesPane.isVisible());
    }
    @FXML
    void closeShiftButtonOnAction() {
        closeShiftPane.setVisible(true);
        startSync = false;
    }
    @FXML
    void openShiftButtonOnAction() {
        openShiftPane.setVisible(true);
    }
    @FXML
    private Pane openShiftPane;
    @FXML
    private Button okOpenShiftButton;
    @FXML
    private Button cancelOpenShiftButton;
    @FXML
    private TextField cashDepositTextField;
    @FXML
    void okOpenShiftButtonOnAction() throws SQLException {
        openShiftAction();
    }
    @FXML
    private void okOpenShiftOnEnterKey(KeyEvent okEvent) throws SQLException {
        if (okEvent.getCode() == KeyCode.ENTER)  {
            openShiftAction();
        }
    }
    public void openShiftAction() throws SQLException {
        String cashDepositString = cashDepositTextField.getText().replace(',', '.');
        cashDepositString = cashDepositString.replace(" ", "");
        double cashDeposit = Double.parseDouble(cashDepositString);
        updateShiftSql(false, cashDeposit);
        for (Button product_button : PRODUCT_BUTTONS) {
            product_button.setDisable(false);
        }
        shiftLog(false);
        startSync = true;
        closeShiftButton.setDisable(false);
        openShiftButton.setDisable(true);
        allSales.setDisable(false);
        openShiftPane.setVisible(false);
    }
    @FXML
    void cancelOpenShiftButtonOnAction() {
        Stage stage = (Stage) cancelOpenShiftButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    void cancelOpenShiftOnEscapeKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE)  {
            LOGGER.log(Level.INFO,"Escape key pressed. Logged out...");
            Stage stage = (Stage) cancelOpenShiftButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private Pane closeShiftPane;
    @FXML
    private Button okCloseShiftButton;
    @FXML
    private Button cancelCloseShiftButton;

    @FXML
    void okCloseShiftButtonOnAction() throws IOException, SQLException {
        todaySalesArrayList.clear();
        allSalesTable.refresh();

        Connection con = getConnection("local_database");
        deleteFromSql(con, "sale_product", "DELETE");
        deleteFromSql(con, "sale", "DELETE");
        updateShiftSql(true, 0.00);
        shiftLog(true);
        try {
            syncShiftLog();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        clearLoadedShiftLog();
        PRODUCT_BUTTONS.clear(); //Кнопки продуктов очистка Array
        NUMBER_BUTTONS.clear(); //Очистка цифровых кнопок Array

        productCategories.clear(); //Очищаем категории продуктов Array
        currentSaleProducts.clear(); //Очищаем список текущих продуктов в списке Array
        products.clear(); //очищаем список Продуктов Array
        discounts.clear(); //очищаем список скидок Array
        paymentTypes.clear(); //очищаем типы оплаты Array
        outlets.clear(); //очищаем список торговых точек Array

        Stage stage = (Stage) closeShiftButton.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeBoardApp.class.getResource("login.fxml"));
        Stage stageLogin = new Stage();
        Scene sceneLogin = new Scene(fxmlLoader.load());
        stageLogin.initStyle(StageStyle.UNDECORATED);
        stageLogin.setTitle("CoffeeApp");
        stageLogin.setScene(sceneLogin);
        closeShiftPane.setVisible(false);
        stageLogin.show();
    }
    @FXML
    void cancelCloseShiftButtonOnAction() {
        startSync = true;
        closeShiftPane.setVisible(false);
    }

    @FXML
    private AnchorPane allSalesPane;
    @FXML
    private TableView<SaleProduct> allSalesTable;
    @FXML
    private TableColumn<TodaySales, Time> timeSalesColumn;
    @FXML
    private TableColumn<TodaySales, String> productSalesColumn;
    @FXML
    private TableColumn<TodaySales, Integer> numberOfUnit;
    @FXML
    private TableColumn<TodaySales, String> unitOfMeasurement;
    @FXML
    private TableColumn<TodaySales, Double> priceSalesColumn;
    @FXML
    private TableColumn<TodaySales, Integer> amountSalesColumn;
    @FXML
    private TableColumn<TodaySales, Integer> discountSalesColumn;
    @FXML
    private TableColumn<TodaySales, Double> sumSalesColumn;
    @FXML
    private TableColumn<TodaySales, String> paymentTypeColumn;

    @FXML
    private Label cashSumSaleLabel;
    @FXML
    private Label cardSumSaleLabel;
    @FXML
    private Label allSumSaleLabel;

    @FXML
    private GridPane mainGridPane;
    private final ArrayList <Button> PRODUCT_BUTTONS = new ArrayList<>();

    EventHandler<ActionEvent> eventProductButtons = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            startSync = false;
            cashReceiptButton.setDisable(true);
            int idProductButton = Integer.parseInt(button.getAccessibleText());

            if (newSale) {
                cancelCashReceiptButton.setDisable(false);
                saleId = UniqueIdGenerator.getId(); //получаем новый уникальный идентификатор продажи (Создаётся уникальный идентификатор нового чека)
                currentSale = new CurrentSale(saleId, UserList.currentUser.getUserId(), currentOutlet.getOutletId()); //Создаётся текущий Чек.
                newSale = false; //Значение boolean true меняется на false. Последующие действия уже не создают новую продажу до момента нажатия на "+" в Панели Текущего продукта.

                for (Product product : products) {
                    if (product.getProductId() == idProductButton) { //Если id продукта соответствует id нажатой кнопки продукта,
                        createCurrentProduct(product);
                        break;
                    }
                }
            } else { //если же продукт в чеке не первый, чек создавать не надо
                for (Product product : products) { //Циклом перебираются все продукты из products ArrayList
                    if (product.getProductId() == idProductButton) { //Если id продукта соответствует id нажатой кнопки продукта,
                        createCurrentProduct(product); //Берём product и делаем ссылку на него в currentProduct
                        break;
                    }
                }
            }
            xLabel.setVisible(true); //После чего делаем видимым значок "X" - количество
            productNameLabel.setVisible(true); //Делаем видимым Наименование продукта
            addProduct.setVisible(true); //Делаем видимой кнопку добавления продукта
            //TODO заменить видимость кнопки добавления продукта на доступность кнопки добавления продукта
            discountButtonActivate.setVisible(true);//То же, что и с кнопкой добавления продукта.
        }
        public void createCurrentProduct(Product product) {
            buttonsIsDisable(PRODUCT_BUTTONS, true); //Сделать кнопки продукта недоступными.
            buttonsIsDisable(NUMBER_BUTTONS, false); //Сделать цифровые кнопки доступными.
            productCategoryIco.setVisible(true); //Иконка продукта отображается (пока без логики).
            //TODO в зависимости от категории продукта или от продукта (это сложнее поддерживать в случае смены ассортимента),
            // иконка Продукта при выборе должна меняться.
            productNameLabel.setText(product.getProduct()); //Рядом с иконкой продукта отображается наименование продукта.
            priceLabel.setText(String.valueOf(product.getPrice())); //Установить стоимость продукта.
            priceLabel.setVisible(true);
            //TODO в пользовательском интерфейсе сделать TextLabel с суммой около информации о выбранном продукте,
            // которая меняется при каждом совершаемом действии (выборе количества, процента скидки и т.д.)
            //Добавляем данные в currentProduct
            currentProduct = new SaleProduct(product.getProductId()
                    , product.getProduct()
                    , product.getPrice());
        }
    };

    @FXML
    private GridPane numbersGridPane;
    private final ArrayList <Button> NUMBER_BUTTONS = new ArrayList<>();

    EventHandler<ActionEvent> eventNumberButtons = new EventHandler<>() { //Действие, запускаемое при нажатии на одну из цифровых кнопок.
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            cashReceiptButton.setDisable(true);

            if (Integer.parseInt(button.getAccessibleText()) != 0) {
                amountLabel.setText(button.getAccessibleText()); //Label количества берёт данные из AccessibleText цифровой кнопки.
                amountLabel.setVisible(true);
                currentProduct.setAmount(Integer.parseInt(button.getAccessibleText())); //для currentProduct устанавливается количество продукта.
                currentProduct.setSum(currentProduct.getPrice() * currentProduct.getAmount()); //сумма стоимости продукта исходя из выбранного количества.
                buttonsIsDisable(NUMBER_BUTTONS, true);
                productOperationButtonsIsDisable(false);
            } else {
                /*
                 * Если гость передумал на данном моменте, нажатие на Цифровую кнопку 0,
                 * производит очистку объекта текущего продукта. Система возвращается в начальное состояние выбора продукта.
                 * При этом, если даже позиция в Чеке первая, новый чек не создаётся, т.к. в данном моменте значение переменной newSale типа boolean всё ещё false.
                 * (данное значение меняется на true только после формирования Чека при закрытии текущей продажи).
                 */
                priceLabel.setVisible(false);
                currentProduct = null; //Текущий продукт становится null.
                productCategoryIco.setVisible(false); //Картинка Продукта перестаёт быть видимой
                xLabel.setVisible(false); //Символ количества перестаёт отображаться
                productNameLabel.setVisible(false); //Название продукта перестаёт отображаться
                amountLabel.setVisible(false); //Количество продукта перестаёт отображаться
                addProduct.setDisable(true); //Кнопка добавления продукта становится неактивной
                productOperationButtonsIsDisable(true); //Кнопки с операциями по текущему продукту становятся недоступными.
                buttonsIsDisable(NUMBER_BUTTONS, true); //Цифровые кнопки становятся недоступными.
                buttonsIsDisable(PRODUCT_BUTTONS,false); //Кнопки с Продуктами становятся активными.
            }
        }
    };

    @FXML
    private TableView<SaleProduct> saleTable; //таблица продажи (текущий чек)
    @FXML
    private TableColumn<SaleProduct, String> productColumn; //колонка с наименованием продукта
    @FXML
    private TableColumn<SaleProduct, Double> priceColumn; //колонка со стоимостью продукта
    @FXML
    private TableColumn<SaleProduct, Integer> amountColumn; //количество продукта
    @FXML
    private TableColumn<SaleProduct, Integer> discountColumn;
    @FXML
    private TableColumn<SaleProduct, Double> sumColumn; //сумма стоимости продукта исходя из количества
    @FXML
    private Label sumLabel; //Сумма стоимости товара
    //TODO добавить сумму Чека без учёта скидки и сумму чека со скидкой (итог)
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель текущего Продукта.
     * Здесь происходят операции с Продуктом, который ещё не добавлен в текущий чек ("+").
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
    private Label priceLabel; //Отображение цены выбранного товара
    @FXML
    private Label xLabel; //Символ X количество товара
    @FXML
    private Label amountLabel; //Цифровое отображение количества товара
    @FXML
    private Label productNameLabel; //наименование продукта
    @FXML
    private Button addProduct; //кнопка добавления продукта в текущий чек (Зелёный плюс)
    @FXML
    private Button discountButtonActivate;
    @FXML
    private Button delProduct; //"С" - удалить продукт (действует так же как и 0???)
    @FXML
    private Button cashReceiptButton; //сформировать чек.
    @FXML
    private Button cancelCashReceiptButton; //отменить чек.

    @FXML
    void discountButtonActivateOnAction() {
        //discountPanel, на которой находятся discountGrid, DISCOUNT_BUTTONS становится видимой и доступной пользователю для дальнейших действий
        discountPanel.setVisible(true);
    }
    //На данный момент кнопка не имеет действия.
    @FXML
    void delProductOnAction() {
        //TODO кнопка отмены ButtonOnAction
    }

    //Логика при нажатии на кнопку "+" добавления продукта в текущий чек.
    @FXML
    void addProductOnAction() {
        if (currentProduct.getDiscountId() == 0) {
            for (Discount discount : discounts) {
                if (discount.getDiscount() == 0) {
                    currentProduct.setDiscountId(discount.getDiscountId());
                    break;
                }
            }
        }
        SaleProductList.addProductToArray(positionsCount, currentProduct);
        positionsCount++;
        priceLabel.setVisible(false);
        cashReceiptButton.setDisable(false); //Кнопка Чек становится доступна
        saleTable.setItems(saleProductsObservableList);
        saleTable.refresh();

        productCategoryIco.setVisible(false);
        xLabel.setVisible(false);
        sumLabelRefresh();
        productNameLabel.setVisible(false);
        amountLabel.setVisible(false);
        addProduct.setDisable(true);
        productOperationButtonsIsDisable(true);
        buttonsIsDisable(PRODUCT_BUTTONS, false);
    }
    @FXML
    void cashReceiptOnAction() {
        cashReceiptButton.setDisable(false);
        paymentTypePanel.setVisible(true);
    }
    @FXML
    void cancelCashReceiptOnAction() {
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
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
    private GridPane discountGridPane;

    private final ArrayList<Button> DISCOUNT_BUTTONS = new ArrayList<>();

    EventHandler<ActionEvent> eventDiscountButtons = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            discountPanel.setVisible(false);

            for (SaleProduct currentSaleProduct : currentSaleProducts) {
                currentSaleProduct.setDiscountId(Integer.parseInt(button.getAccessibleText()));
                for (Discount discount : discounts) {
                    if (currentSaleProduct.getDiscountId() == discount.getDiscountId()) {
                        currentSaleProduct.setDiscount(discount.getDiscount());
                    }
                }
                currentSaleProduct.setSum(currentSaleProduct.getPrice()
                - (currentSaleProduct.getPrice() * currentSaleProduct.getDiscount() / 100)
                        * currentSaleProduct.getAmount());
            }
            saleTable.setItems(saleProductsObservableList);
            saleTable.refresh();
            sumLabelRefresh();
        }
    };
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель Чека (Выбор типа оплаты).
     * Данная панель открывается поверх панели Текущего товара нажатием на кнопку "Чек" находящуюся в панели
     * текущего товара.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private AnchorPane paymentTypePanel;

    @FXML
    Button[] paymentTypeButtons = new Button[2];

    @FXML
    private Button paymentType1;

    @FXML
    private Button paymentType2;

    @FXML
    private Button endThisTaleAnother1;

    /* Кнопка произвольной суммы чека
     * В случае, если Клиенту не хватает какой-то суммы, то можно ввести сколько есть.
     * Дальше в действии разница делится на все позиции и отражается в Чеке.
     * После этого нужно снова выбрать тип оплаты (наличные/карта).
     */
    @FXML
    private Button oups;

    @FXML
    void oupsOnAction() {
        correctionPane.setVisible(true);
    }

    @FXML
    void paymentTypeOnAction(ActionEvent event) throws SQLException {
        Button button = (Button) event.getSource();
        currentSale.setPaymentTypeId(Integer.parseInt(button.getAccessibleText()));
        cashReceiptButton.setDisable(true);
        long nowDate = System.currentTimeMillis();
        Date saleDate = new Date(nowDate);
        long nowTime = System.currentTimeMillis();
        Time saleTime = new Time(nowTime);
        currentSale.setCurrentDate(saleDate);
        currentSale.setCurrentTime(saleTime);
        currentSale.setClientId(1); //Временное назначение клиента

        if (Integer.parseInt(button.getAccessibleText()) == 1) {
            changePane.setVisible(true);
            paymentTypePanel.setVisible(false);
        } else if (Integer.parseInt(button.getAccessibleText()) == 2) {
            startSync = true;
            allSalesTable.setItems(todaySalesObservableList);
            allSalesTable.refresh();
            Connection conLocalDB = getConnection("local_database");
            addSaleToLocalDB(conLocalDB, currentSale); //Добавляем текущую продажу в SQLite
            addSaleProductsToLocalDB(conLocalDB, currentSaleProducts, currentSale); //Добавляем продукты текущей продажи в SQLite
            currentSale = null;
            newSale = true;
            positionsCount = 0;
            currentSaleProducts.clear();
            sumLabel.setText("0.00");
            saleTable.refresh();
            paymentTypePanel.setVisible(false);
            renameCashReceiptButton();


        }
    }
    @FXML
    void endThisTaleAnother1OnAction() {
        paymentTypePanel.setVisible(false);
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель вопроса о сдаче //TODO Сделать логику выбора "со сдачей/без сдачи"
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private Pane changePane;
    @FXML
    private Button noChange;
    @FXML
    private Button withChangeButton;
    @FXML
    private Button cancelChangeButton;

    @FXML
    void cancelChangeButtonOnAction() {
        changePane.setVisible(false);
        cashReceiptButton.setDisable(false);
    }

    @FXML
    void noChangeOnAction() throws SQLException {
        startSync = true;
        allSalesTable.setItems(todaySalesObservableList);
        allSalesTable.refresh();
        Connection conLocalDB = getConnection("local_database");
        addSaleToLocalDB(conLocalDB, currentSale); //Добавляем текущую продажу в SQLite
        addSaleProductsToLocalDB(conLocalDB, currentSaleProducts, currentSale); //Добавляем продукты текущей продажи в SQLite
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        saleTable.refresh();
        changePane.setVisible(false);
        paymentTypePanel.setVisible(false);
        renameCashReceiptButton();
    }

    @FXML
    void withChangeOnAction() {
        changePane.setVisible(false);
        withChangePane.setVisible(true);
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Считаем сдачу
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private Pane withChangePane;

    @FXML
    private TextField sumChangeTextField;

    @FXML
    private Label changeLabel;

    @FXML
    private Button okWithChangeButton;
    @FXML
    private Button cancelWithChangeButton;

    @FXML
    void cancelWithChangeOnAction() {
        withChangePane.setVisible(false);
        paymentTypePanel.setVisible(false);
    }

    @FXML
    void okWithChangeOnAction() throws SQLException {
        startSync = true;
        allSalesTable.setItems(todaySalesObservableList);
        allSalesTable.refresh();
        Connection conLocalDB = getConnection("local_database");
        addSaleToLocalDB(conLocalDB, currentSale); //Добавляем текущую продажу в SQLite
        addSaleProductsToLocalDB(conLocalDB, currentSaleProducts, currentSale); //Добавляем продукты текущей продажи в SQLite
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        saleTable.refresh();

        renameCashReceiptButton();

        withChangePane.setVisible(false);
        sumChangeTextField.clear();
        changeLabel.setText("0.00");
    }

    @FXML
    private AnchorPane correctionPane;
    @FXML
    private Button correctionButton;
    @FXML
    private Button cancelOupsButton;
    @FXML
    private TextField correctionTextField;

    @FXML
    void correctionButtonOnAction() {
        double startSum = Double.parseDouble(sumLabel.getText());
        String endSumString = correctionTextField.getText().replace(',', '.');
        endSumString = endSumString.replace(" ", "");

        double endSum = Double.parseDouble(endSumString);
        int productsInArray = currentSaleProducts.size();
        double correctionDiscount = (startSum - endSum) / productsInArray;
        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            for (Product product : products) {
                if (currentSaleProduct.getProductId() == product.getProductId()) {
                    currentSaleProduct.setSum(currentSaleProduct.getSum() - correctionDiscount);
                }
            }
        }
        saleTable.setItems(saleProductsObservableList);
        saleTable.refresh();
        correctionTextField.setText(null);
        double total = 0.0;

        for (SaleProduct saleProduct : saleTable.getItems()) {
            total = total + saleProduct.getSum();
        }
        sumLabel.setText(String.valueOf(total));
        correctionPane.setVisible(false);
    }

    @FXML
    void cancelOupsButtonOnAction() {
        correctionPane.setVisible(false);
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Инициализация
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    void initialize() throws SQLException {
        Thread clockThread = new Thread(() -> {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String time = simpleDateFormat.format(new java.util.Date());
                Platform.runLater(() -> timeLabel.setText(time));
            }
        });   clockThread.setDaemon(true);
        clockThread.start();

        Thread syncTestThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(300000); //5 min
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    if (startSync) {
                        Connection con;
                        try {
                            con = getConnection("network_database");
                            isOnline(true);
                            try {
                                syncShiftLog();
                                syncSales();
                                syncSalesProduct();
                                con.close();
                            } catch (SQLException | ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (SQLException e) {
                            isOnline(false);
                        }
                    }
                });
            }
        });
        syncTestThread.setDaemon(true);
        syncTestThread.start();


        networkIndicator.setFill(Color.YELLOW);
        //Синхронизация и загрузка списка продуктов
        Connection conNetworkProductDB = null;
        Connection conLocalProductDB;
        try {
            conNetworkProductDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.INFO,"Connection to network database failed");
        }

        conLocalProductDB = getConnection("local_database");

        if (conNetworkProductDB != null) {
            isOnline(true);
            syncProductsList(conNetworkProductDB, conLocalProductDB);
            conNetworkProductDB.close();
        } else {
            isOnline(false);
        }
        createProductsList(conLocalProductDB);
        conLocalProductDB.close();

        //Синхронизация и загрузка категорий продуктов
        Connection conNetworkCategoryDB = null;
        Connection conLocalCategoryDB;
        try {
            conNetworkCategoryDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.INFO,"Connection to network database failed");
        }
        conLocalCategoryDB = getConnection("local_database");
        if (conNetworkCategoryDB != null) {
            isOnline(true);
            syncProductCategoriesList(conNetworkCategoryDB, conLocalCategoryDB);
            conNetworkCategoryDB.close();
        } else {
            isOnline(false);
        }
        createProductCategoriesList(conLocalCategoryDB);
        conLocalCategoryDB.close();

        //Синхронизация и загрузка типов оплаты
        Connection conNetworkPaymentTypeDB = null;
        Connection conLocalPaymentTypeDB;
        try {
            conNetworkPaymentTypeDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.INFO,"Connection to network database failed");
        }
        conLocalPaymentTypeDB = getConnection("local_database");
        if (conNetworkPaymentTypeDB != null) {
            isOnline(true);
            syncPaymentTypesList(conNetworkPaymentTypeDB, conLocalPaymentTypeDB);
            conNetworkPaymentTypeDB.close();
            createPaymentTypeList(conLocalPaymentTypeDB);
            conLocalPaymentTypeDB.close();
        } else {
            isOnline(false);
            createPaymentTypeList(conLocalPaymentTypeDB);
            conLocalCategoryDB.close();
        }

        Connection conNetworkDiscountDB = null;
        Connection conLocalDiscountDB;
        try {
            conNetworkDiscountDB = getConnection("network_database");
        } catch (SQLException sqlEx) {
            LOGGER.log(Level.INFO,"Connection to network database failed");
        }
        conLocalDiscountDB = getConnection("local_database");
        if (conNetworkDiscountDB != null) {
            isOnline(true);
            syncDiscountsList(conNetworkDiscountDB, conLocalDiscountDB);
            conNetworkDiscountDB.close();
        } else {
            isOnline(false);
        }
        createDiscountList(conLocalDiscountDB);
        conLocalDiscountDB.close();

        paymentTypePanel.setVisible(false);
        closeShiftButton.setDisable(false);
        paymentTypeButtons[0] = paymentType1;
        paymentTypeButtons[1] = paymentType2;

        int count = 0;
        for (PaymentType paymentType : paymentTypes) {
            paymentTypeButtons[count].setAccessibleText(String.valueOf(paymentType.getPaymentTypeId()));
            paymentTypeButtons[count].setText(paymentType.getPaymentType());
            count++;
        }
        saleProductsObservableList.addListener((ListChangeListener<SaleProduct>) change -> {});

        for (int i = 0; i < numbersGridPane.getColumnCount(); i++) {
            Button numberButton = new Button();
            NUMBER_BUTTONS.add(i, numberButton);
            int finalI = i;
            NUMBER_BUTTONS.get(i).layoutBoundsProperty().addListener((observable, oldValue, newValue) -> NUMBER_BUTTONS.get(finalI).setFont(Font.font(Math.sqrt(newValue.getHeight() * 10))));
            NUMBER_BUTTONS.get(i).setWrapText(true);
            NUMBER_BUTTONS.get(i).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
            NUMBER_BUTTONS.get(i).setPrefSize(91.0, 91.0);
            NUMBER_BUTTONS.get(i).setVisible(true);
            GridPane.setConstraints(NUMBER_BUTTONS.get(i), i, 0);
            numbersGridPane.getChildren().add(NUMBER_BUTTONS.get(i));
            NUMBER_BUTTONS.get(i).setOnAction(eventNumberButtons);

            if (i < 9) {
                NUMBER_BUTTONS.get(i).setText(String.valueOf(i + 1));
                NUMBER_BUTTONS.get(i).setAccessibleText(String.valueOf(i + 1));
            } else {
                NUMBER_BUTTONS.get(i).setText("0");
                NUMBER_BUTTONS.get(i).setAccessibleText("0");
            }

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
        buttonsIsDisable(NUMBER_BUTTONS, true);
        cashReceiptButton.setDisable(true);
        cancelCashReceiptButton.setDisable(true);

        //Размещаем кнопки с актуальными скидками.
        int countD = 0;
        discountGridPane.getHgap();
        discountGridPane.getVgap();
        for (int l = 0; l < discountGridPane.getColumnCount(); l++) {
            for (int h = 0; h < discountGridPane.getRowCount(); h++) {
                Button discountButton = new Button();
                DISCOUNT_BUTTONS.add(countD, discountButton);
                int finalDiscountButtonsCount = countD;
                DISCOUNT_BUTTONS.get(countD).layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
                        DISCOUNT_BUTTONS.get(finalDiscountButtonsCount).setFont(
                                Font.font(Math.sqrt(newValue.getHeight() * 1.5))));
                DISCOUNT_BUTTONS.get(countD).setWrapText(true);
                DISCOUNT_BUTTONS.get(countD).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
                DISCOUNT_BUTTONS.get(countD).setPrefSize(75.0, 75.0);
                DISCOUNT_BUTTONS.get(countD).setVisible(false);
                GridPane.setConstraints(DISCOUNT_BUTTONS.get(countD), l, h);
                discountGridPane.getChildren().add(DISCOUNT_BUTTONS.get(countD));
                DISCOUNT_BUTTONS.get(countD).setOnAction(eventDiscountButtons);
                countD++;
            }
        }

        discountNameButtons(DISCOUNT_BUTTONS); //именуем кнопки


        /*____________________________________________________________________________________
         * Здесь происходит инициализация столбцов таблицы текущей продажи.
         * В неё добавляются позиции Продуктов.
         * setCellValueFactory определяет что добавляется и в какой столбец.
         ____________________________________________________________________________________*/
        saleTable.setEditable(true);
        productColumn.setEditable(true);
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        priceColumn.setEditable(true);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setEditable(true);
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        saleTable.setItems(saleProductsObservableList);
        discountColumn.setEditable(true);
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        sumColumn.setEditable(true);
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));

        /*
        Здесь мы создаём таблицу с продажами за сегодняшний день.
         */

        timeSalesColumn.setCellValueFactory(new PropertyValueFactory<>("saleTime"));
        productSalesColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        numberOfUnit.setCellValueFactory(new PropertyValueFactory<>("numberOfUnit")); //кол-во ед.
        unitOfMeasurement.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasurement")); //ед.
        priceSalesColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountSalesColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        discountSalesColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        sumSalesColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        allSalesTable.setItems(todaySalesObservableList);

        //Подсчитываем количество Продуктов в каждой категории.
        for (Product product : products) {
            for (ProductCategory productCategory : productCategories) {
                if (product.getCategory() == productCategory.getProductCategoryId()) {
                    productCategory.setAmountProducts(productCategory.getAmountProducts() + 1);
                }
            }
        }

        //Размещаем кнопки Продуктов в GridPane
        int countP = 0;
        for (int l = 0; l < mainGridPane.getColumnCount(); l++) {

            for (int h = 0; h < mainGridPane.getRowCount(); h++) {
                Button productButton = new Button();
                PRODUCT_BUTTONS.add(countP, productButton);
                int finalProdButtonsCount = countP;
                PRODUCT_BUTTONS.get(countP).layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
                        PRODUCT_BUTTONS.get(finalProdButtonsCount).setFont(
                                Font.font(Math.sqrt(newValue.getHeight() * 1.5))));
                PRODUCT_BUTTONS.get(countP).setWrapText(true);
                PRODUCT_BUTTONS.get(countP).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
                PRODUCT_BUTTONS.get(countP).setPrefSize(91.0, 91.0);
                PRODUCT_BUTTONS.get(countP).setVisible(false);
                GridPane.setConstraints(PRODUCT_BUTTONS.get(countP), l, h);
                mainGridPane.getChildren().add(PRODUCT_BUTTONS.get(countP));
                PRODUCT_BUTTONS.get(countP).setOnAction(eventProductButtons);
                countP++;
            }
        }
        productNameButton(PRODUCT_BUTTONS);

        sumChangeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String changeSumString = newValue.replace(',', '.');
            changeSumString = changeSumString.replace(" ", "");
            double change;
            if (changeSumString.isEmpty()) {
                change = 0.0;
            } else {
                change = Double.parseDouble(changeSumString) - (Double.parseDouble(sumLabel.getText()));
            }
            if (change < 0.0) {
                changeLabel.setText("0.0");
            } else {
                changeLabel.setText(String.valueOf(change));
            }
        });
        saleTable.setPlaceholder(new Label("Выберете продукт"));
        allSalesTable.setPlaceholder(new Label("В текущей смене ещё нет продаж"));

        //Проверка открыта смена или нет.
        if (checkShift()) {
            for (Button product_button : PRODUCT_BUTTONS) {
                product_button.setDisable(true);
                startSync = false;
            }
            closeShiftButton.setDisable(true);
            allSales.setDisable(true);
        } else {
            openShiftButton.setDisable(true);
        }

        cashDepositTextField.textProperty().addListener(observable -> okOpenShiftButton.setDisable(false));

    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/


    public void buttonsIsDisable(ArrayList<Button> buttons, boolean res) {
        for (Button button : buttons) {
            button.setDisable(res);
        }
    }
    void sumLabelRefresh() {
        double total = 0.0;

        for (SaleProduct saleProduct : saleTable.getItems()) {
            total = total + saleProduct.getSum();
        }
        sumLabel.setText(String.valueOf(total));
    }

    /**
     * Данный метод делает кнопки с действиями с Добавляемым продуктом активными/неактивными
     * Например: кнопки Работы с Выбранным продуктом недоступны пока не выбран продукт.
     * @param isDisable - тип boolean, который работает как переключатель доступности.
     */
    public void productOperationButtonsIsDisable(boolean isDisable) {
        addProduct.setDisable(isDisable);
        delProduct.setDisable(isDisable);
    }
    public void isOnline(boolean status) {
        if (status) {
            networkIndicatorLabel.setText("в сети");
            networkIndicator.setFill(Color.GREEN);
        } else {
            networkIndicatorLabel.setText("не в сети");
            networkIndicator.setFill(Color.YELLOW);
        }
    }
    public void renameCashReceiptButton() {
        cashReceiptButton.setFont(Font.font("", FontWeight.BOLD, 29));
        cashReceiptButton.setText("Чек сформирован");
        Thread renameCashReceiptButton = new Thread(() -> {
            try {
                Thread.sleep(4000); //4 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                cashReceiptButton.setText("Чек");
                cashReceiptButton.setFont(Font.font("System", FontWeight.BOLD, 35));
            });
        });   renameCashReceiptButton.setDaemon(true); //Это закроет поток. Он сообщает JVM, что это фоновый поток, поэтому он завершится при выходе.
        renameCashReceiptButton.start(); //Запускаем поток.
    }
}