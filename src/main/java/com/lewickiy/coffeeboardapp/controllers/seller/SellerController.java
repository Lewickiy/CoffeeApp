package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.controllers.actions.Direction;
import com.lewickiy.coffeeboardapp.controllers.actions.worktable.WorkTableChoice;
import com.lewickiy.coffeeboardapp.dao.todaySales.TodaySales;
import com.lewickiy.coffeeboardapp.entities.currentsale.CurrentSale;
import com.lewickiy.coffeeboardapp.entities.discount.Discount;
import com.lewickiy.coffeeboardapp.entities.product.Product;
import com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct;
import com.lewickiy.coffeeboardapp.entities.user.User;
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

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.controllers.actions.ButtonsActivity.buttonsIsDisable;
import static com.lewickiy.coffeeboardapp.controllers.actions.ButtonsOnGridPane.buttonsOnGridPane;
import static com.lewickiy.coffeeboardapp.controllers.actions.CheckShift.checkShift;
import static com.lewickiy.coffeeboardapp.controllers.actions.Correction.correctionSum;
import static com.lewickiy.coffeeboardapp.controllers.actions.CurrentSaleSum.currentSaleSum;
import static com.lewickiy.coffeeboardapp.controllers.actions.DiscountAction.makeDiscount;
import static com.lewickiy.coffeeboardapp.controllers.actions.DiscountNameButton.discountNameButtons;
import static com.lewickiy.coffeeboardapp.controllers.actions.PaymentTypeNameButton.paymentTypeNameButton;
import static com.lewickiy.coffeeboardapp.controllers.actions.ProductNameButton.productNameButton;
import static com.lewickiy.coffeeboardapp.controllers.actions.TemporaryRenameButton.tempRenameButton;
import static com.lewickiy.coffeeboardapp.controllers.actions.worktable.WorkTable.enterToWorkTable;
import static com.lewickiy.coffeeboardapp.controllers.seller.actions.ShiftCloser.closeShift;
import static com.lewickiy.coffeeboardapp.dao.connector.NetworkIndicator.isOnline;
import static com.lewickiy.coffeeboardapp.dao.query.DeleteSaleProduct.deleteSaleProduct;
import static com.lewickiy.coffeeboardapp.dao.query.OpenCloseShift.updateShiftSql;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.shiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.syncShiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.SyncCorrected.syncCorrectedSales;
import static com.lewickiy.coffeeboardapp.dao.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.dao.query.SyncSales.syncSales;
import static com.lewickiy.coffeeboardapp.dao.sync.BusinessData.syncBusinessData;
import static com.lewickiy.coffeeboardapp.dao.sync.ShiftData.syncShiftData;
import static com.lewickiy.coffeeboardapp.dao.todaySales.LitersOfDrinks.countLitersOfDrinks;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodayCashDeposit.getCashDeposit;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesList.todaySalesArrayList;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesListReload.todaySalesListReload;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesSumAll.sumAll;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesSumCard.sumCard;
import static com.lewickiy.coffeeboardapp.dao.todaySales.TodaySalesSumCash.sumCash;
import static com.lewickiy.coffeeboardapp.entities.currentsale.CurrentSale.addToLocalDB;
import static com.lewickiy.coffeeboardapp.entities.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.entities.info.Info.isNewMessage;
import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;
import static com.lewickiy.coffeeboardapp.entities.product.ProductsInCategory.countingProductsInCategory;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProduct.addSaleProductsToLocalDB;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.addProductToArray;
import static com.lewickiy.coffeeboardapp.entities.saleproduct.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.currentUser;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.users;

public class SellerController {
    public static boolean startSync = true;
    private long saleId;
    private int positionsCount;
    private boolean newSale = true;
    private CurrentSale currentSale;
    private SaleProduct currentProduct;
    private final SaleProduct DELETE_PRODUCT = new SaleProduct();
    private User selectedUser;

    private final List<Button> NUMBER_BUTTONS = new ArrayList<>();
    private final List<Button> PRODUCT_BUTTONS = new ArrayList<>();
    private final List<Button> DISCOUNT_BUTTONS = new ArrayList<>();

    private final ObservableList<User> USERS_OBSERVABLE_LIST = FXCollections.observableList(users);
    private final ObservableList<SaleProduct> SALE_PRODUCT_OBSERVABLE_LIST = FXCollections.observableList(currentSaleProducts);
    private final ObservableList<SaleProduct> TODAY_SALES_OBSERVABLE_LIST = FXCollections.observableList(todaySalesArrayList);

    @FXML
    private AnchorPane discountPane;
    @FXML
    private AnchorPane paymentTypePane;
    @FXML
    private AnchorPane allSalesPane;
    @FXML
    private AnchorPane correctionPane;
    @FXML
    private AnchorPane usersPane;

    @FXML
    private ImageView productCategoryIco;

    @FXML
    private Circle networkIndicator;

    @FXML
    private CheckBox activeStaffACheckBox;
    @FXML
    private CheckBox administratorACheckBox;

    @FXML
    private GridPane discountGridPane;
    @FXML
    private GridPane mainGridPane;
    @FXML
    private GridPane numbersGridPane;

    @FXML
    private Pane workAdminPane;
    @FXML
    private Pane productsAdminPane;
    @FXML
    private Pane infoAdminPane;
    @FXML
    private Pane openShiftPane;
    @FXML
    private Pane closeShiftPane;
    @FXML
    private Pane mainReceiptPane;
    @FXML
    private Pane changePane;
    @FXML
    private Pane withChangePane;
    @FXML
    private AnchorPane mainAdminPane;

    @FXML
    private Label allCashLabel;
    @FXML
    private Label allSumSaleLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label cardSumSaleLabel;
    @FXML
    private Label cashSumSaleLabel;
    @FXML
    private Label cashDepositLabel;
    @FXML
    private Label changeLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private Label productNameLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label sumLabel;
    @FXML
    private Label headerLabel;
    @FXML
    private Label litresLabel;
    @FXML
    private Label usernameAllSalesLabel;
    @FXML
    private Label xLabel;
    @FXML
    private Label networkIndicatorLabel;

    @FXML
    private final Button[] PAYMENT_TYPE_BUTTONS = new Button[2];
    @FXML
    private Button adminButton;
    @FXML
    private Button edtUserButton;
    @FXML
    private Button showActiveUsersButton;
    @FXML
    private Button botButton;
    @FXML
    private Button closeShiftButton;
    @FXML
    private Button openShiftButton;
    @FXML
    private Button allSales;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button okOpenShiftButton;
    @FXML
    private Button cancelOpenShiftButton;
    @FXML
    private Button okCloseShiftButton;
    @FXML
    private Button addProduct;
    @FXML
    private Button discountButtonActivate;
    @FXML
    private Button cashReceiptButton;
    @FXML
    private Button cButton;
    @FXML
    private Button paymentType1;
    @FXML
    private Button paymentType2;

    @FXML
    private TextField firstNameATextField;
    @FXML
    private TextField lastNameATextField;
    @FXML
    private TextField loginATextField;
    @FXML
    private TextField correctionTextField;
    @FXML
    private TextField patronymicATextField;
    @FXML
    private TextField phoneATextField;
    @FXML
    private TextField sumChangeTextField;
    @FXML
    private TextField cashDepositTextField;

    @FXML
    private PasswordField passwordAPasswordField;

    @FXML
    private DatePicker birthdayADatePicker;

    @FXML
    void regularCustomerButtonOnAction() {
        //TODO
    }
    @FXML
    void addUserButtonOnAction() {
        edtUserButton.setDisable(true);
        showActiveUsersButton.setDisable(true);
        usersTable.setDisable(true);

        loginATextField.clear();
        firstNameATextField.clear();
        lastNameATextField.clear();
        patronymicATextField.clear();
        passwordAPasswordField.clear();
        phoneATextField.clear();
        administratorACheckBox.setSelected(false);
        activeStaffACheckBox.setSelected(false);
        birthdayADatePicker.setValue(LocalDate.now());
    }

    @FXML
    void editUserButtonOnAction() {
        //TODO
    }
    @FXML
    void showActiveUsersButtonOnAction() {
        //TODO
    }
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Boolean> userActiveStuffColumn = new TableColumn<>("activeStuff");
    @FXML
    private TableColumn<User, Boolean> userAdministratorColumn;
    @FXML
    private TableColumn<User, Date> userBirthdayColumn;
    @FXML
    private TableColumn<User, String> userFirstNameColumn;
    @FXML
    private TableColumn<User, String> userLastNameColumn;
    @FXML
    private TableColumn<User, String> userLoginColumn;
    @FXML
    private TableColumn<User, String> userPasswordColumn;
    @FXML
    private TableColumn<User, String> userPatronymicColumn;
    @FXML
    private TableColumn<User, String> userPhoneColumn;

    @FXML
    void referenceBooksButtonOnAction() {
        productsAdminPane.setVisible(!productsAdminPane.isVisible());
    }

    @FXML
    void usersButtonOnAction() {
        usersPane.setVisible(!usersPane.isVisible());
    }
    @FXML
    void adminButtonOnAction() {
        if (!mainAdminPane.isVisible()) { //открываем Admin
            mainAdminPane.setVisible(true);
            infoAdminPane.setVisible(true);
            workAdminPane.setVisible(true);
            cButton.setVisible(false);

            openShiftButton.setDisable(true);
            closeShiftButton.setDisable(true);
            botButton.setDisable(true);
            allSales.setDisable(true);
            headerLabel.setText("Журнал:");

            cButtonOnAction();
            buttonsIsDisable(PRODUCT_BUTTONS, true);

            startSync = false;
        } else {
            mainAdminPane.setVisible(false);
            infoAdminPane.setVisible(false);
            workAdminPane.setVisible(false);
            cButton.setVisible(true);

            openShiftButton.setDisable(false);
            closeShiftButton.setDisable(false);
            botButton.setDisable(false);
            allSales.setDisable(false);
            headerLabel.setText("Заказ:");

            buttonsIsDisable(PRODUCT_BUTTONS, false);
            startSync = true;
        }
    }
    @FXML
    void botButtonOnAction() {
        botButton.setStyle("-fx-background-color: LightGREEN;");
        //TODO
    }
    @FXML
    void editButtonOnAction() {
        saveButton.setDisable(false);
        editButton.setDisable(true);
    }
    @FXML
    void saveButtonOnAction() {
        deleteSaleProduct(DELETE_PRODUCT);
        todaySalesArrayList.clear();
        allSalesPaneRefresh();
        saveButton.setDisable(true);
        editButton.setDisable(true);
    }
    @FXML
    void allSalesOnAction() {
        if (!allSalesPane.isVisible()) {
            allSalesPaneRefresh();
        } else {
            todaySalesArrayList.clear();
            saveButton.setDisable(true);
            editButton.setDisable(true);
        }
        allSalesPane.setVisible(!allSalesPane.isVisible());
    }
    @FXML
    void closeShiftButtonOnAction() {
        closeShiftPane.setVisible(true);
        okCloseShiftButton.requestFocus();
        startSync = false;
    }
    @FXML
    void openShiftButtonOnAction() {
        openShiftPane.setVisible(true);
    }
    @FXML
    void okOpenShiftButtonOnAction() {
        cashDepositTextField.setDisable(true);
        okOpenShiftButton.setDisable(true);
        cancelOpenShiftButton.setDisable(true);
        openShiftAction();
    }
    @FXML
    private void OpenShiftOnKey(KeyEvent okEvent) {
        if (okEvent.getCode() == KeyCode.ENTER && !okOpenShiftButton.isDisable())  {
            cashDepositTextField.setDisable(true);
            okOpenShiftButton.setDisable(true);
            cancelOpenShiftButton.setDisable(true);
            openShiftAction();
        } else if (okEvent.getCode() == KeyCode.ESCAPE) {
            Stage stage = (Stage) cashDepositTextField.getScene().getWindow();
            stage.close();
        }
    }
    public void openShiftAction() {
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
    void cancelCloseShiftButtonOnAction() {
        startSync = true;
        closeShiftPane.setVisible(false);
    }
    @FXML
    void okCloseShiftButtonOnAction() {
        Stage stage = (Stage) okCloseShiftButton.getScene().getWindow();
        closeShift(PRODUCT_BUTTONS, NUMBER_BUTTONS);
        enterToWorkTable(WorkTableChoice.LOGIN, closeShiftButton);
        stage.close();
    }
    @FXML
    void closeShiftOnKey(KeyEvent closeShiftOnKey) {
        if (closeShiftOnKey.getCode() == KeyCode.ESCAPE)  {
            startSync = true;
            closeShiftPane.setVisible(false);
        } else if (closeShiftOnKey.getCode() == KeyCode.ENTER) {
            Stage stage = (Stage) okCloseShiftButton.getScene().getWindow();
            closeShift(PRODUCT_BUTTONS, NUMBER_BUTTONS);
            enterToWorkTable(WorkTableChoice.LOGIN, closeShiftButton);
            stage.close();
        }
    }
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
                currentSale = new CurrentSale(saleId, UserList.currentUser.getId(), currentOutlet.getOutletId());
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
        mainReceiptPane.setVisible(false);
        buttonsIsDisable(PRODUCT_BUTTONS, true);
        cashReceiptButton.setDisable(true);
        addProduct.setDisable(true);
        cashReceiptButton.setDisable(false);
        paymentTypePane.setVisible(true);
    }
    @FXML
    void cButtonOnAction() {
        mainReceiptPane.setVisible(true);
        sumChangeTextField.clear();
        changeLabel.setText("0.00");
        paymentTypePane.setVisible(false);
        discountPane.setVisible(false);
        changePane.setVisible(false);
        withChangePane.setVisible(false);
        correctionPane.setVisible(false);
        cashReceiptButton.setDisable(true);
        buttonsIsDisable(NUMBER_BUTTONS, PRODUCT_BUTTONS, true, false);

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
    @FXML
    void oupsOnAction() {
        correctionPane.setVisible(true);
    }
    @FXML
    void paymentTypeOnAction(ActionEvent event) {
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
    @FXML
    void noChangeOnAction() {
        endSaleOperation();
    }
    private void endSaleOperation() {
        mainReceiptPane.setVisible(true);
        startSync = true;
        allSalesTable.setItems(TODAY_SALES_OBSERVABLE_LIST);
        allSalesTable.refresh();
        addToLocalDB(currentSale);
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
        tempRenameButton(cashReceiptButton, "Чек сформирован", "Чек", 4);
    }

    @FXML
    void withChangeOnAction() {
        changePane.setVisible(false);
        withChangePane.setVisible(true);
    }
    @FXML
    void okWithChangeOnAction() {
        endSaleOperation();
        cButton.setDisable(true);
        withChangePane.setVisible(false);
        sumChangeTextField.clear(); //в cButton
        changeLabel.setText("0.00"); //в cButton
        buttonsIsDisable(PRODUCT_BUTTONS, false);
    }

    @FXML
    void correctionButtonOnAction() {
        correctionSum(sumLabel.getText(), correctionTextField.getText());
        correctionTextField.setText(null);

        saleTable.setItems(SALE_PRODUCT_OBSERVABLE_LIST);
        saleTable.refresh();

        sumLabel.setText(String.valueOf(currentSaleSum()));
        correctionPane.setVisible(false);
    }

    @FXML
    void initialize() {
        isOnline(networkIndicatorLabel, networkIndicator);
        syncBusinessData();

        if (currentUser.isAdministrator()) {
            adminButton.setVisible(true);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String time = simpleDateFormat.format(new java.util.Date());
        clockLabel.setText(time);

        usersTable.setEditable(true);
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        userPatronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userLoginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        userBirthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        userPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userAdministratorColumn.setCellValueFactory(new PropertyValueFactory<>("administrator"));
        userActiveStuffColumn.setCellValueFactory(new PropertyValueFactory<>("activeStuff"));
        usersTable.setItems(USERS_OBSERVABLE_LIST);
        usersTable.setPlaceholder(new Label("Отсутствуют пользователи. Проверьте подключение."));

        TableView.TableViewSelectionModel<User> selectionModelUsersTable = usersTable.getSelectionModel();
        selectionModelUsersTable.selectedItemProperty().addListener((observableValue, user, t1) -> {
            selectedUser = new User();
            selectedUser.setId(t1.getId());
            selectedUser.setLogin(t1.getLogin());
            loginATextField.setText(selectedUser.getLogin());//
            selectedUser.setPassword(t1.getPassword());
            passwordAPasswordField.setText(selectedUser.getPassword());//
            selectedUser.setFirstName(t1.getFirstName());
            firstNameATextField.setText(selectedUser.getFirstName());//
            selectedUser.setLastName(t1.getLastName());
            lastNameATextField.setText(selectedUser.getLastName());//
            selectedUser.setPatronymic(t1.getPatronymic());
            patronymicATextField.setText(selectedUser.getPatronymic());//
            selectedUser.setPhone(t1.getPhone());
            phoneATextField.setText(selectedUser.getPhone());
            selectedUser.setBirthday(t1.getBirthday());
            selectedUser.setAdministrator(t1.isAdministrator());
            administratorACheckBox.setSelected(selectedUser.isAdministrator());
            selectedUser.setActiveStuff(t1.isActiveStuff());
            activeStaffACheckBox.setSelected(selectedUser.isActiveStuff());

            birthdayADatePicker.setOnAction(e -> {
//                LocalDate date = birthdayADatePicker.getValue();
            });
        });
        usernameAllSalesLabel.setText(currentUser.getFirstName() + "!");
        networkIndicator.setFill(Color.GOLD);

        AtomicInteger syncCount = new AtomicInteger();
        Timeline syncTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(60),
                        event -> {
                            SimpleDateFormat simpleDateFormatSync = new SimpleDateFormat("HH:mm");
                            String timeSync = simpleDateFormatSync.format(new java.util.Date());
                            clockLabel.setText(timeSync);
                            if (startSync && syncCount.get() % 5 == 0) {
                                if (isNewMessage()) {
                                    enterToWorkTable(WorkTableChoice.INFO);
                                }
                                syncShiftData();
                            }
                            syncCount.getAndIncrement();

                        }));
        syncTimeLine.setCycleCount(Timeline.INDEFINITE);
        syncTimeLine.play();

        PAYMENT_TYPE_BUTTONS[0] = paymentType1;
        PAYMENT_TYPE_BUTTONS[1] = paymentType2;

        paymentTypeNameButton(PAYMENT_TYPE_BUTTONS);

        SALE_PRODUCT_OBSERVABLE_LIST.addListener((ListChangeListener<SaleProduct>) change -> {});

        buttonsOnGridPane(Direction.HORIZONTAL, numbersGridPane, NUMBER_BUTTONS, 91.0, eventNumberButtons);

        buttonsIsDisable(NUMBER_BUTTONS, true);

        buttonsOnGridPane(Direction.VERTICAL, discountGridPane, DISCOUNT_BUTTONS, 75.0, eventDiscountButtons);
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
            DELETE_PRODUCT.setId(t1.getId());
            DELETE_PRODUCT.setProductId(t1.getProductId());
            editButton.setDisable(false);
        });

        countingProductsInCategory();

        buttonsOnGridPane(Direction.VERTICAL, mainGridPane, PRODUCT_BUTTONS, 91.0, eventProductButtons);
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

    public void allSalesPaneRefresh() {
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