package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.controllers.login.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.controllers.seller.actions.Direction;
import com.lewickiy.coffeeboardapp.dao.connector.Database;
import com.lewickiy.coffeeboardapp.dao.todaySales.TodaySales;
import com.lewickiy.coffeeboardapp.entities.currentsale.CurrentSale;
import com.lewickiy.coffeeboardapp.entities.discount.Discount;
import com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentType;
import com.lewickiy.coffeeboardapp.entities.product.Product;
import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;
import com.lewickiy.coffeeboardapp.entities.user.UserList;
import com.lewickiy.coffeeboardapp.idgenerator.UniqueIdGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
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
import static com.lewickiy.coffeeboardapp.dao.connector.DatabaseConnector.getConnection;
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.*;
import static com.lewickiy.coffeeboardapp.dao.info.syncInfo.getInfoMessage;
import static com.lewickiy.coffeeboardapp.dao.query.OpenCloseShift.updateShiftSql;
import static com.lewickiy.coffeeboardapp.dao.query.Query.deleteFromLocalSql;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.shiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.syncShiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.SyncCorrected.syncCorrectedSales;
import static com.lewickiy.coffeeboardapp.dao.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.dao.query.SyncSales.syncSales;
import static com.lewickiy.coffeeboardapp.dao.todaySales.LitersOfDrinks.countLitersOfDrinks;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodayCashDeposit.getCashDeposit;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesList.todaySalesArrayList;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesListReload.todaySalesListReload;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesSumAll.sumAll;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesSumCard.sumCard;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesSumCash.sumCash;
import static com.lewickiy.coffeeboardapp.entities.currentsale.CurrentSale.addSaleToLocalDB;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.createPaymentTypeList;
import static com.lewickiy.coffeeboardapp.entities.paymenttype.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.createProductCategoriesList;
import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.createProductsList;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.entities.product.ProductsInCategory.countingProductsInCategory;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct.addSaleProductsToLocalDB;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.addProductToArray;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.currentSaleProducts;
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
    private long saleId;
    public static boolean startSync = true;
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
        try (Connection conLocal = getConnection(Database.LOCAL_DB)) {
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
            allSalesPaneRefresh();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        saveButton.setDisable(true);
        editButton.setDisable(true);
    }
    @FXML
    void allSalesOnAction() throws SQLException, ParseException {
        if (!allSalesPane.isVisible()) {
            allSalesPaneRefresh();
        } else {
            todaySalesArrayList.clear();
        }
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
    void okOpenShiftButtonOnAction() throws SQLException, ParseException {
        openShiftAction();
    }
    @FXML
    private void okOpenShiftOnEnterKey(KeyEvent okEvent) throws SQLException, ParseException {
        if (okEvent.getCode() == KeyCode.ENTER)  {
            openShiftAction();
        }
    }
    public void openShiftAction() throws SQLException, ParseException {
        String cashDepositString = cashDepositTextField.getText().replace(',', '.');
        cashDepositString = cashDepositString.replace(" ", "");
        double cashDeposit = Double.parseDouble(cashDepositString);
        updateShiftSql(false, cashDeposit);
        for (Button product_button : PRODUCT_BUTTONS) {
            product_button.setDisable(false);
        }
        shiftLog(false);

        syncShiftLog();
        syncSales();
        syncSalesProduct();
        syncCorrectedSales();

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
    void okCloseShiftButtonOnAction() throws ParseException {
        Stage stage = (Stage) okCloseShiftButton.getScene().getWindow();
        todaySalesArrayList.clear();
        allSalesTable.refresh();

        Connection conNDB;
        try {
            conNDB = getConnection(Database.NETWORK_DB);
            conNDB.close();
            updateShiftSql(true, 0.00);
            shiftLog(true);
            syncShiftLog();
            syncSales();
            syncSalesProduct();
            syncCorrectedSales();
            Connection con = getConnection(Database.LOCAL_DB);
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

        }catch (SQLException sqlException) {
            LOGGER.log(Level.WARNING,"Failed to connect to network database when closing shift. Database synchronization failed.");
            stage.close();
        }



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
        networkIndicator.setFill(Color.GOLD);

        startClockThread(clockLabel, 1);

        AtomicInteger tempCount = new AtomicInteger(); //Временно. Удалить.

        Timeline syncTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(300),
                        event -> {
                            LOGGER.log(Level.INFO,"startSync is " + startSync);
                            if (startSync) {
                                Connection con;
                                try {
                                    con = getConnection(Database.NETWORK_DB);
                                    try {
                                        isOnline(networkIndicatorLabel, networkIndicator, true);
                                        con.close();
                                        getInfoMessage();
                                        syncCorrectedSales();
                                        syncShiftLog();
                                        syncSales();
                                        syncSalesProduct();
                                        System.out.println(tempCount.getAndIncrement()); //Временно. Удалить.
                                    } catch (SQLException sqlEx) {
                                        isOnline(networkIndicatorLabel, networkIndicator,false);
                                        System.out.println("sqlEx");
                                    } catch (ParseException pEx) {
                                        System.out.println("pex");
                                        isOnline(networkIndicatorLabel, networkIndicator,false);

                                    }
                                } catch (SQLException e) {
                                    isOnline(networkIndicatorLabel, networkIndicator,false);
                                }
                            }
                        }));
        syncTimeLine.setCycleCount(Timeline.INDEFINITE);
        syncTimeLine.play();

        Connection conNDB;
        Connection conLDB = getConnection(Database.LOCAL_DB);
        try {
            LOGGER.log(Level.INFO,"Start network database synchronization on Seller desktop startup");
            conNDB = getConnection(Database.NETWORK_DB);
            isOnline(networkIndicatorLabel, networkIndicator,true);
            syncProductsList(conNDB, conLDB);
            syncProductCategoriesList(conNDB, conLDB);
            syncPaymentTypesList(conNDB, conLDB);
            syncDiscountsList(conNDB, conLDB);
            createProductsList(conLDB);
            createProductCategoriesList(conLDB);
            createPaymentTypeList(conLDB);
            createDiscountList(conLDB);
            // При появлении синхронизации каких-либо дополнительных данных. Метод вносить сюда, ниже* и в отдельный поток синхронизации.
            conNDB.close();
            LOGGER.log(Level.FINE, "Synchronization completed successfully");
        } catch (SQLException sqlException) {
            LOGGER.log(Level.WARNING,"When starting to synchronize with the network database at Seller desktop startup, it was not possible to connect to the network. Data is taken from local database");
            isOnline(networkIndicatorLabel, networkIndicator,false);
            createProductsList(conLDB);
            createProductCategoriesList(conLDB);
            createPaymentTypeList(conLDB);
            createDiscountList(conLDB);
            //*
            LOGGER.log(Level.INFO, "Further work may be carried out with outdated data");
        } finally {
            conLDB.close();
        }

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

        buttonsIsDisable(NUMBER_BUTTONS, true);

        buttonsOnGridPane(Direction.VERTICAL, discountGridPane, DISCOUNT_BUTTONS, eventDiscountButtons);
        discountNameButtons(DISCOUNT_BUTTONS);

        productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        sumColumn.setCellValueFactory(new PropertyValueFactory<>("sum"));
        saleTable.setItems(SALE_PRODUCT_OBSERVABLE_LIST);
        saleTable.setPlaceholder(new Label("Выберете продукт"));

        allSalesTable.setEditable(true);
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
            System.out.println(DELETE_PRODUCT.getSaleId() + " " + DELETE_PRODUCT.getProductId());
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

    public void allSalesPaneRefresh() throws SQLException, ParseException {
        todaySalesListReload();
        allSalesTable.setItems(TODAY_SALES_OBSERVABLE_LIST);
        allSalesTable.refresh();
        cashSumSaleLabel.setText(sumCash() + " руб.");
        cardSumSaleLabel.setText(sumCard() + " руб.");
        allSumSaleLabel.setText(sumAll() + " руб.");
        cashDepositLabel.setText(getCashDeposit() + " руб.");
        allCashLabel.setText((sumCash() + getCashDeposit()) + " руб.");
        litresLabel.setText(countLitersOfDrinks() + " л.");
    }
}