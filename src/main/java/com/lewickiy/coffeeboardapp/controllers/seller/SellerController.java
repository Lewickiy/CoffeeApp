package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.controllers.seller.actions.Direction;
import com.lewickiy.coffeeboardapp.database.discount.Discount;
import com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySales;
import com.lewickiy.coffeeboardapp.database.product.Product;
import com.lewickiy.coffeeboardapp.entities.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.entities.paymentType.PaymentType;
import com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct;
import com.lewickiy.coffeeboardapp.entities.user.UserList;
import com.lewickiy.coffeeboardapp.idgenerator.UniqueIdGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.stage.Stage;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.actions.TemporaryRenameButton.tempRenameButton;
import static com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTable.enterToWorkTable;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.ButtonsOnGridPane.buttonsOnGridPane;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.CheckShift.checkShift;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.ClockThread.startClockThread;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.Correction.correctionSum;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.CurrentSaleSum.currentSaleSum;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.DiscountAction.makeDiscount;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.DiscountNameButton.discountNameButtons;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.NetworkIndicator.isOnline;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.ProductNameButton.productNameButton;
import static com.lewickiy.coffeeboardapp.database.Query.deleteFromLocalSql;
import static com.lewickiy.coffeeboardapp.database.connection.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.database.local.SyncLocalDB.*;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.LitersOfDrinks.countLitersOfDrinks;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodayCashDeposit.getCashDeposit;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesList.todaySalesArrayList;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesListReload.todaySalesListReload;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesSumAll.sumAll;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesSumCard.sumCard;
import static com.lewickiy.coffeeboardapp.database.local.todaySales.TodaySalesSumCash.sumCash;
import static com.lewickiy.coffeeboardapp.database.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.createProductCategoriesList;
import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.createProductsList;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.database.product.ProductsInCategory.countingProductsInCategory;
import static com.lewickiy.coffeeboardapp.database.query.OpenCloseShift.updateShiftSql;
import static com.lewickiy.coffeeboardapp.database.query.ShiftLog.shiftLog;
import static com.lewickiy.coffeeboardapp.database.query.ShiftLog.syncShiftLog;
import static com.lewickiy.coffeeboardapp.database.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.database.query.SyncSales.syncSales;
import static com.lewickiy.coffeeboardapp.entities.currentSale.CurrentSale.addSaleToLocalDB;
import static com.lewickiy.coffeeboardapp.entities.paymentType.PaymentTypeList.createPaymentTypeList;
import static com.lewickiy.coffeeboardapp.entities.paymentType.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProduct.addSaleProductsToLocalDB;
import static com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProductList.addProductToArray;
import static com.lewickiy.coffeeboardapp.entities.saleProduct.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.users;

public class SellerController {
    @FXML
    private Button botButton;
    @FXML
    void botButtonOnAction() {
        //TODO
        botButton.setStyle("-fx-background-color: LightGREEN;");
    }

    //___________________Временная реализация кнопки с ботом выше #bot______________________________
    private boolean newSale = true;
    private int saleId;
    private boolean startSync = true;
    private int positionsCount;
    private CurrentSale currentSale;
    private SaleProduct currentProduct;
    private final SaleProduct DELETE_PRODUCT = new SaleProduct();
    private final ObservableList<SaleProduct> SALE_PRODUCT_OBSERVABLE_LIST = FXCollections.observableList(currentSaleProducts);
    private final ObservableList<SaleProduct> TODAY_SALES_OBSERVABLE_LIST = FXCollections.observableList(todaySalesArrayList);
    @FXML
    private Label clockLabel;
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
    private Button editButton;
    @FXML
    void editButtonOnAction() {
        saveButton.setDisable(false);
        editButton.setDisable(true);
    }
    @FXML
    private Button saveButton;
    @FXML
    void saveButtonOnAction() throws ParseException {
        try (Connection conLocal = getConnection("local_database")) {
            String update = "UPDATE sale_product SET corrected = ? WHERE sale_id = "
                    + DELETE_PRODUCT.getSaleId()
                    + " AND " + "product_id = "
                    + DELETE_PRODUCT.getProductId();
            PreparedStatement prepareStatement = conLocal.prepareStatement(update);
            prepareStatement.setInt(1, 1);
            prepareStatement.executeUpdate();
            prepareStatement.close();
            conLocal.close();
            todaySalesArrayList.clear();
            todaySalesListReload();
            allSalesTable.setItems(TODAY_SALES_OBSERVABLE_LIST);
            allSalesTable.refresh();
            cashSumSaleLabel.setText(sumCash() + " руб.");
            cardSumSaleLabel.setText(sumCard() + " руб.");
            allSumSaleLabel.setText(sumAll() + " руб.");
            cashDepositLabel.setText(getCashDeposit() + " руб.");
            allCashLabel.setText((sumCash() + getCashDeposit()) + " руб.");
            litresLabel.setText(countLitersOfDrinks() + " л.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        saveButton.setDisable(true);
        editButton.setDisable(true);
    }
    @FXML
    void allSalesOnAction() throws SQLException, ParseException {
        todaySalesListReload();
        allSalesTable.setItems(TODAY_SALES_OBSERVABLE_LIST);
        allSalesTable.refresh();
        cashSumSaleLabel.setText(sumCash() + " руб.");
        cardSumSaleLabel.setText(sumCard() + " руб.");
        allSumSaleLabel.setText(sumAll() + " руб.");
        cashDepositLabel.setText(getCashDeposit() + " руб.");
        allCashLabel.setText((sumCash() + getCashDeposit()) + " руб.");
        if (allSalesPane.isVisible()) {
            todaySalesArrayList.clear();
        }
        litresLabel.setText(String.valueOf(countLitersOfDrinks()));
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
    void okCloseShiftButtonOnAction() throws SQLException, ParseException {
        //TODO проверка. Если данные не загрузились в network database, не очищать соответствующие таблицы local database.
        todaySalesArrayList.clear();
        allSalesTable.refresh();

        updateShiftSql(true, 0.00);
        shiftLog(true);
        syncShiftLog();
        syncSales();
        syncSalesProduct();

        Connection con = getConnection("local_database");
        deleteFromLocalSql(con, "sale_product");
        deleteFromLocalSql(con, "sale");

        PRODUCT_BUTTONS.clear();
        NUMBER_BUTTONS.clear();

        productCategories.clear();
        currentSaleProducts.clear();
        products.clear();
        discounts.clear();
        paymentTypes.clear();
        outlets.clear();
        users.clear();
        enterToWorkTable(WorkTableChoice.LOGIN, closeShiftButton);
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
            xLabel.setVisible(true);
            productNameLabel.setVisible(true);
            addProduct.setVisible(true);
            discountButtonActivate.setVisible(true);
        }
        public void createCurrentProduct(Product product) {
            buttonsIsDisable(PRODUCT_BUTTONS, true);
            buttonsIsDisable(NUMBER_BUTTONS, false);
            productCategoryIco.setVisible(true);
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
                amountLabel.setText(button.getAccessibleText());
                amountLabel.setVisible(true);
                currentProduct.setAmount(Integer.parseInt(button.getAccessibleText()));
                currentProduct.setSum(currentProduct.getPrice() * currentProduct.getAmount());
                buttonsIsDisable(NUMBER_BUTTONS, true);
                addProduct.setDisable(false);
            } else {
                priceLabel.setVisible(false);
                currentProduct = null;
                productCategoryIco.setVisible(false);
                xLabel.setVisible(false);
                productNameLabel.setVisible(false);
                amountLabel.setVisible(false);
                addProduct.setDisable(true);
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

    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель текущего Продукта.
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
    private Button cashReceiptButton;
    @FXML
    private Button cButton;

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
        addProductToArray(positionsCount, currentProduct);
        positionsCount++;
        priceLabel.setVisible(false);
        cashReceiptButton.setDisable(false); //Кнопка Чек становится доступна
        saleTable.setItems(SALE_PRODUCT_OBSERVABLE_LIST);
        saleTable.refresh();

        productCategoryIco.setVisible(false);
        xLabel.setVisible(false);
        sumLabel.setText(String.valueOf(currentSaleSum()));
        productNameLabel.setVisible(false);
        amountLabel.setVisible(false);
        addProduct.setDisable(true);
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
        startSync = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        saleTable.refresh();

        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        cButton.setDisable(true);
        saleTable.refresh();
        sumLabel.setText(String.valueOf(currentSaleSum()));
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель скидок на позицию.
     * Данная панель открывается поверх панели Текущего товара нажатием на кнопку "%" находящуюся в панели
     * текущего товара.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private AnchorPane discountPane;
    @FXML
    private GridPane discountGridPane;
    private final ArrayList<Button> DISCOUNT_BUTTONS = new ArrayList<>();
    EventHandler<ActionEvent> eventDiscountButtons = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            discountPane.setVisible(false);
            makeDiscount(button);
            saleTable.setItems(SALE_PRODUCT_OBSERVABLE_LIST);
            saleTable.refresh();
            sumLabel.setText(String.valueOf(currentSaleSum()));
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
    private final Button[] PAYMENT_TYPE_BUTTONS = new Button[2];
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
            endSaleOperation();
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
        endSaleOperation();
    }

    private void endSaleOperation() throws SQLException {
        startSync = true;
        allSalesTable.setItems(TODAY_SALES_OBSERVABLE_LIST);
        allSalesTable.refresh();
        addSaleToLocalDB(currentSale);
        addSaleProductsToLocalDB(currentSaleProducts, currentSale);
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
        tempRenameButton(cashReceiptButton, "Чек сформирован", 4);
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
        endSaleOperation();

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

        saleTable.setItems(SALE_PRODUCT_OBSERVABLE_LIST);
        saleTable.refresh();

        sumLabel.setText(String.valueOf(currentSaleSum()));
        correctionPane.setVisible(false);
    }

    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Инициализация
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    void initialize() throws SQLException {
        startClockThread(clockLabel, 1);

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
                                isOnline(networkIndicatorLabel, networkIndicator, true);
                                con.close();
                                syncShiftLog();
                                syncSales();
                                syncSalesProduct();
                            } catch (SQLException | ParseException e) {
                                throw new RuntimeException(e);
                            }
                        } catch (SQLException e) {
                            isOnline(networkIndicatorLabel, networkIndicator,false);
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
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncProductsList(conNetworkProductDB, conLocalProductDB);
            conNetworkProductDB.close();
        } else {
            isOnline(networkIndicatorLabel, networkIndicator,false);
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
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncProductCategoriesList(conNetworkCategoryDB, conLocalCategoryDB);
            conNetworkCategoryDB.close();
        } else {
            isOnline(networkIndicatorLabel, networkIndicator,false);
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
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncPaymentTypesList(conNetworkPaymentTypeDB, conLocalPaymentTypeDB);
            conNetworkPaymentTypeDB.close();
            createPaymentTypeList(conLocalPaymentTypeDB);
            conLocalPaymentTypeDB.close();
        } else {
            isOnline(networkIndicatorLabel, networkIndicator,false);
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
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncDiscountsList(conNetworkDiscountDB, conLocalDiscountDB);
            conNetworkDiscountDB.close();
        } else {
            isOnline(networkIndicatorLabel, networkIndicator,false);
        }
        createDiscountList(conLocalDiscountDB);
        conLocalDiscountDB.close();

        paymentTypePane.setVisible(false);
        closeShiftButton.setDisable(false);
        PAYMENT_TYPE_BUTTONS[0] = paymentType1;
        PAYMENT_TYPE_BUTTONS[1] = paymentType2;

        int count = 0;
        for (PaymentType paymentType : paymentTypes) {
            PAYMENT_TYPE_BUTTONS[count].setAccessibleText(String.valueOf(paymentType.getPaymentTypeId()));
            PAYMENT_TYPE_BUTTONS[count].setText(paymentType.getPaymentType());
            count++;
        }
        SALE_PRODUCT_OBSERVABLE_LIST.addListener((ListChangeListener<SaleProduct>) change -> {});

        buttonsOnGridPane(Direction.HORIZONTAL, numbersGridPane, NUMBER_BUTTONS, eventNumberButtons);

        addProduct.setTooltip(new Tooltip("Добавить продукт в чек"));
        cButton.setTooltip(new Tooltip("Пока эта кнопка вообще всё отменяет, но будет сделана кнопка с шагом назад"));

        productCategoryIco.setVisible(false);
        xLabel.setVisible(false);
        amountLabel.setVisible(false);
        productNameLabel.setVisible(false);
        addProduct.setDisable(true);
        buttonsIsDisable(NUMBER_BUTTONS, true);
        cashReceiptButton.setDisable(true);
        cButton.setDisable(true);

        buttonsOnGridPane(Direction.VERTICAL, discountGridPane, DISCOUNT_BUTTONS, eventDiscountButtons);
        discountNameButtons(DISCOUNT_BUTTONS);

        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        saleTable.setItems(SALE_PRODUCT_OBSERVABLE_LIST);
        saleTable.setPlaceholder(new Label("Выберете продукт"));

        timeSalesColumn.setCellValueFactory(new PropertyValueFactory<>("saleTime"));
        productSalesColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        numberOfUnit.setCellValueFactory(new PropertyValueFactory<>("numberOfUnit"));
        unitOfMeasurement.setCellValueFactory(new PropertyValueFactory<>("unitOfMeasurement"));
        priceSalesColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountSalesColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        discountSalesColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        sumSalesColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        paymentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        allSalesTable.setItems(TODAY_SALES_OBSERVABLE_LIST);
        allSalesTable.setPlaceholder(new Label("В текущей смене ещё нет продаж"));

        TableView.TableViewSelectionModel<SaleProduct> selectionModel = allSalesTable.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observableValue, saleProduct, t1) -> {
            DELETE_PRODUCT.setSaleId(t1.getSaleId());
            DELETE_PRODUCT.setProductId(t1.getProductId());
            editButton.setDisable(false);
        });

        countingProductsInCategory();

        buttonsOnGridPane(Direction.VERTICAL, mainGridPane, PRODUCT_BUTTONS, eventProductButtons);
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
        cashDepositTextField.textProperty().addListener(observable
                -> okOpenShiftButton.setDisable(false));
    }

    public void buttonsIsDisable(ArrayList<Button> buttons, boolean res) {
        for (Button button : buttons) {
            button.setDisable(res);
        }
    }
}