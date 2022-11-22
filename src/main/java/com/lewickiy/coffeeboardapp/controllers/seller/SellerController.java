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
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.Correction.correctionSum;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.DiscountAction.makeDiscount;
import static com.lewickiy.coffeeboardapp.database.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.Query.deleteFromSql;
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
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.CheckShift.checkShift;
import static com.lewickiy.coffeeboardapp.database.query.OpenCloseShift.updateShiftSql;
import static com.lewickiy.coffeeboardapp.database.query.ShiftLog.*;
import static com.lewickiy.coffeeboardapp.database.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.database.query.SyncSales.syncSales;

public class SellerController {
    //TODO ArrayLists зафиксировать изначальный размер
    //TODO Исключения залогировать и выдать пользователю user friendly сообщение.
    private boolean newSale = true;
    private int saleId;
    private boolean startSync = true;
    @FXML
    private Label clockLabel;
    private int positionsCount;
    private CurrentSale currentSale;
    private SaleProduct currentProduct;
    static ObservableList<SaleProduct> saleProductsObservableList = FXCollections.observableList(currentSaleProducts);
    static ObservableList<SaleProduct> todaySalesObservableList = FXCollections.observableList(todaySalesArrayList);
    @FXML
    private Circle networkIndicator;
    @FXML
    private Label networkIndicatorLabel;
    @FXML
    private Button closeShiftButton;
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
    void okCloseShiftButtonOnAction() throws IOException, SQLException, ParseException {
        //TODO проверка. Если данные не загрузились в network database, не очищать соответствующие таблицы local database.
        todaySalesArrayList.clear();
        allSalesTable.refresh();

        updateShiftSql(true, 0.00);
        shiftLog(true);
        syncShiftLog();
        syncSales();
        syncSalesProduct();

        //TODO и чеки с продажами тоже можно оставлять. Только новые будут помечаться как 0, загруженные как 1, а не от этой смены - 2 например
        Connection con = getConnection("local_database");
        deleteFromSql(con, "sale_product", "DELETE");
        deleteFromSql(con, "sale", "DELETE");

        PRODUCT_BUTTONS.clear();
        NUMBER_BUTTONS.clear();

        productCategories.clear();
        currentSaleProducts.clear();
        products.clear();
        discounts.clear();
        paymentTypes.clear();
        outlets.clear();

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
                cButton.setDisable(false);
                saleId = UniqueIdGenerator.getId();
                currentSale = new CurrentSale(saleId, UserList.currentUser.getUserId(), currentOutlet.getOutletId());
                newSale = false;

                for (Product product : products) {
                    if (product.getProductId() == idProductButton) {
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
            discountButtonActivate.setVisible(true);
        }
        public void createCurrentProduct(Product product) {
            buttonsIsDisable(PRODUCT_BUTTONS, true);
            buttonsIsDisable(NUMBER_BUTTONS, false);
            productCategoryIco.setVisible(true); //Иконка продукта отображается (пока без логики).
            //TODO в зависимости от категории продукта или от продукта (это сложнее поддерживать в случае смены ассортимента),
            // иконка Продукта при выборе должна меняться.
            productNameLabel.setText(product.getProduct());
            priceLabel.setText(String.valueOf(product.getPrice()));
            priceLabel.setVisible(true);
            currentProduct = new SaleProduct(product.getProductId()
                    , product.getProduct()
                    , product.getPrice());
        }
    };

    @FXML
    private GridPane numbersGridPane;
    private final ArrayList <Button> NUMBER_BUTTONS = new ArrayList<>();

    EventHandler<ActionEvent> eventNumberButtons = new EventHandler<>() {
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
                currentProduct = null;
                productCategoryIco.setVisible(false);
                xLabel.setVisible(false);
                productNameLabel.setVisible(false);
                amountLabel.setVisible(false);
                addProduct.setDisable(true);
                productOperationButtonsIsDisable(true);
                buttonsIsDisable(NUMBER_BUTTONS, true);
                buttonsIsDisable(PRODUCT_BUTTONS,false);
            }
        }
    };

    @FXML
    private TableView<SaleProduct> saleTable;
    @FXML
    private TableColumn<SaleProduct, String> productColumn;
    @FXML
    private TableColumn<SaleProduct, Double> priceColumn;
    @FXML
    private TableColumn<SaleProduct, Integer> amountColumn;
    @FXML
    private TableColumn<SaleProduct, Integer> discountColumn;
    @FXML
    private TableColumn<SaleProduct, Double> sumColumn;
    @FXML
    private Label sumLabel;
    @FXML
    private Label discountSumLabel;
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
    private ImageView productCategoryIco;
    @FXML
    private Label priceLabel;
    @FXML
    private Label xLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label productNameLabel;
    @FXML
    private Button addProduct;
    @FXML
    private Button discountButtonActivate;
    @FXML
    private Button cashReceiptButton; //сформировать чек.
    @FXML
    private Button cButton; //отменить чек.

    @FXML
    void discountButtonActivateOnAction() {
        discountPane.setVisible(true);
    }

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
        buttonsIsDisable(PRODUCT_BUTTONS, true);
        cashReceiptButton.setDisable(false);
        paymentTypePane.setVisible(true);
    }
    @FXML
    void cButtonOnAction() {
        paymentTypePane.setVisible(false);
        discountPane.setVisible(false);
        changePane.setVisible(false);
        withChangePane.setVisible(false);
        correctionPane.setVisible(false);
        cashReceiptButton.setDisable(true);
        buttonsIsDisable(NUMBER_BUTTONS, true);
        buttonsIsDisable(PRODUCT_BUTTONS, false);

        productCategoryIco.setVisible(false);
        xLabel.setVisible(false);

        productNameLabel.setVisible(false);
        amountLabel.setVisible(false);
        priceLabel.setText("");
        priceLabel.setVisible(false);
        addProduct.setDisable(true);
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        saleTable.refresh();

        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        cButton.setDisable(true);
        saleTable.refresh();
        sumLabelRefresh();
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель скидок на позицию.
     * Данная панель открывается поверх панели Текущего товара нажатием на кнопку "%" находящуюся в панели
     * текущего товара.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private AnchorPane discountPane; //Непосредственно сама панель. В ней содержатся все актуальные скидки.
    @FXML
    private GridPane discountGridPane;
    private final ArrayList<Button> DISCOUNT_BUTTONS = new ArrayList<>();
    EventHandler<ActionEvent> eventDiscountButtons = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            discountPane.setVisible(false);
            makeDiscount(button);
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
    private AnchorPane paymentTypePane;
    @FXML
    Button[] paymentTypeButtons = new Button[2];
    @FXML
    private Button paymentType1;
    @FXML
    private Button paymentType2;
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
        currentSale.setClientId(1);

        if (Integer.parseInt(button.getAccessibleText()) == 1) {
            changePane.setVisible(true);
            paymentTypePane.setVisible(false);
        } else if (Integer.parseInt(button.getAccessibleText()) == 2) {
            startSync = true;
            allSalesTable.setItems(todaySalesObservableList);
            allSalesTable.refresh();
            Connection conLocalDB = getConnection("local_database");
            addSaleToLocalDB(conLocalDB, currentSale);
            addSaleProductsToLocalDB(conLocalDB, currentSaleProducts, currentSale);
            currentSale = null;
            newSale = true;
            positionsCount = 0;
            currentSaleProducts.clear();
            sumLabel.setText("0.00");
            saleTable.refresh();
            paymentTypePane.setVisible(false);
            cButton.setDisable(true);
            buttonsIsDisable(PRODUCT_BUTTONS, false);
            renameCashReceiptButton(4);
        }
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель вопроса о сдаче.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private Pane changePane;
    @FXML
    void noChangeOnAction() throws SQLException {
        startSync = true;
        allSalesTable.setItems(todaySalesObservableList);
        allSalesTable.refresh();
        Connection conLocalDB = getConnection("local_database");
        addSaleToLocalDB(conLocalDB, currentSale);
        addSaleProductsToLocalDB(conLocalDB, currentSaleProducts, currentSale);
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        saleTable.refresh();
        changePane.setVisible(false);
        paymentTypePane.setVisible(false);
        cButton.setDisable(true);
        buttonsIsDisable(PRODUCT_BUTTONS, false);
        renameCashReceiptButton(4);
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
    void okWithChangeOnAction() throws SQLException {
        startSync = true;
        allSalesTable.setItems(todaySalesObservableList);
        allSalesTable.refresh();
        Connection conLocalDB = getConnection("local_database");
        addSaleToLocalDB(conLocalDB, currentSale);
        addSaleProductsToLocalDB(conLocalDB, currentSaleProducts, currentSale);
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        saleTable.refresh();

        renameCashReceiptButton(4);
        cButton.setDisable(true);
        withChangePane.setVisible(false);
        sumChangeTextField.clear();
        changeLabel.setText("0.00");
        buttonsIsDisable(PRODUCT_BUTTONS, false);
    }

    @FXML
    private AnchorPane correctionPane;
    @FXML
    private TextField correctionTextField;

    @FXML
    void correctionButtonOnAction() {
        correctionSum(sumLabel.getText(), correctionTextField.getText());
        correctionTextField.setText(null);

        saleTable.setItems(saleProductsObservableList);
        saleTable.refresh();

        double total = 0.0;

        for (SaleProduct saleProduct : saleTable.getItems()) {
            total = total + saleProduct.getSum();
        }
        sumLabel.setText(String.valueOf(total));
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
                Platform.runLater(() -> clockLabel.setText(time));
            }
        });   clockThread.setDaemon(true);
        clockThread.start();

        Thread syncThread = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(300000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    if (startSync) {
                        Connection con;
                        try {
                            con = getConnection("network_database");
                            try {
                                isOnline(true);
                                con.close();
                                syncShiftLog();
                                syncSales();
                                syncSalesProduct();
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
        syncThread.setDaemon(true);
        syncThread.start();

        networkIndicator.setFill(Color.YELLOW);
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

        paymentTypePane.setVisible(false);
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
        addProduct.setTooltip(new Tooltip("Добавить продукт в чек"));
        cButton.setTooltip(new Tooltip("Пока эта кнопка вообще всё отменяет, но будет сделана кнопка с шагом назад"));

        productCategoryIco.setVisible(false);
        xLabel.setVisible(false);
        amountLabel.setVisible(false);
        productNameLabel.setVisible(false);
        productOperationButtonsIsDisable(true);
        buttonsIsDisable(NUMBER_BUTTONS, true);
        cashReceiptButton.setDisable(true);
        cButton.setDisable(true);

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

        discountSumLabel.setVisible(false);

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
    }
    void isOnline(boolean status) {
        if (status) {
            networkIndicatorLabel.setText("в сети");
            networkIndicator.setFill(Color.GREEN);
        } else {
            networkIndicatorLabel.setText("не в сети");
            networkIndicator.setFill(Color.YELLOW);
        }
    }
    /**
     * Temporarily renames the "Receipt" button to "Receipt generated".<br>
     * @param sec takes a time in seconds as a parameter.<br>
     */
    public void renameCashReceiptButton(int sec) {
        int millis = sec * 1000;
        cashReceiptButton.setFont(Font.font("", FontWeight.BOLD, 29));
        cashReceiptButton.setText("Чек сформирован");
        Thread renameCashReceiptButton = new Thread(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                cashReceiptButton.setText("Чек");
                cashReceiptButton.setFont(Font.font("System", FontWeight.BOLD, 35));
            });
        });   renameCashReceiptButton.setDaemon(true);
        renameCashReceiptButton.start();
    }
}