package com.lewickiy.coffeeboardapp.controllers.seller;

import com.lewickiy.coffeeboardapp.CoffeeBoardApp;
import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList;
import com.lewickiy.coffeeboardapp.database.discount.Discount;
import com.lewickiy.coffeeboardapp.database.outlet.Outlet;
import com.lewickiy.coffeeboardapp.database.paymentType.PaymentType;
import com.lewickiy.coffeeboardapp.database.product.Product;
import com.lewickiy.coffeeboardapp.database.product.ProductCategory;
import com.lewickiy.coffeeboardapp.database.user.UserList;
import com.lewickiy.coffeeboardapp.idgenerator.UniqueIdGenerator;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.controllers.seller.DiscountNameButton.discountNameButtons;
import static com.lewickiy.coffeeboardapp.controllers.seller.ProductNameButton.productNameButton;
import static com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale.createNewSale;
import static com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct.addProductsToSale;
import static com.lewickiy.coffeeboardapp.database.currentSale.SaleProductList.currentSaleProducts;
import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.createDiscountList;
import static com.lewickiy.coffeeboardapp.database.discount.DiscountList.discounts;
import static com.lewickiy.coffeeboardapp.database.local.LocalBase.*;
import static com.lewickiy.coffeeboardapp.database.outlet.OutletList.outlets;
import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.createPaymentTypeAL;
import static com.lewickiy.coffeeboardapp.database.paymentType.PaymentTypeList.paymentTypes;
import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.createProductCategoriesList;
import static com.lewickiy.coffeeboardapp.database.product.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.database.product.ProductList.products;

public class SellerController {

    private boolean newSale = true; //boolean значение необходимости создания нового чека

    private int saleId; //Идентификатор текущей продажи. Создаётся в классе UniqueIdGenerator

    private int positionsCount;

    private CurrentSale currentSale; //объект - текущая продажа.

    private SaleProduct currentProduct; //объект - зона сбора данных.

    static ObservableList<SaleProduct> saleProductsObservableList = FXCollections.observableList(currentSaleProducts);

    /*____________________________________start___________________________________________
     * Панель информации в верхней части экрана.
     * Здесь присутствуют кнопки:
     * Закрытие смены -
     * ...
     * А также метод логики нажатия на кнопку Закрытия смены
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private Button closeShiftButton; //кнопка закрытия смены

    @FXML
    private Button allSales;

    @FXML
    private AnchorPane allSalesPane;

    @FXML
    private Label userEarnings; //Не действует. Должно помещаться на отдельном окне при закрытии смены.

    @FXML
    void allSalesOnAction() {
        if (allSalesPane.isVisible()) {
            allSalesPane.setVisible(false);
        } else {
            allSalesPane.setVisible(true);
        }

    }
    //Действие при нажатии на кнопку Закрытия смены.
    @FXML
    void closeShiftButtonOnAction() throws IOException, SQLException {
        writeSqlFromLocalDb();
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
        stageLogin.show();
    }
    /*____________________________________˄˄˄_____________________________________________
     *___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель кнопок продуктов
     *_____________________________________˅˅˅____________________________________________*/
    @FXML
    private GridPane mainGridPane;

    private ArrayList <Button> productButtons = new ArrayList<>(); //ArrayList объектов Button

    EventHandler<ActionEvent> eventProductButtons = new EventHandler<ActionEvent>() { //Поведение по нажатию кнопки с Продуктом
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource(); //Получить данные от нажатой кнопки
            int idProductButton = Integer.parseInt(button.getAccessibleText()); //Записывается id нажатой кнопки (Продукт)

            if (newSale) { //Если данный продукт в Чеке первый, то...
                endThisTaleAnother.setDisable(false);
                saleId = UniqueIdGenerator.getId(); //получаем новый уникальный идентификатор продажи (Создаётся уникальный идентификатор нового чека)
                currentSale = new CurrentSale(saleId, UserList.currentUser.getUserId(), Outlet.currentOutlet.getOutletId()); //Создаётся текущий Чек.
                newSale = false; //Значение boolean true меняется на false. Последующие действия уже не создают новую продажу до момента нажатия на "+" в Панели Текущего продукта.
                /*
                 * Здесь мы будем вставлять позицию в SaleProductList ПРИ СОЗДАНИИ НОВОГО ЧЕКА. Пока без загрузки в базу.
                 * Циклом перебираются все продукты из products ArrayList, соответствующий условию (совпадение по id) передаётся currentProduct
                 */
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
            buttonsIsDisable(productButtons, true); //Сделать кнопки продукта недоступными.
            buttonsIsDisable(numberButtons, false); //Сделать цифровые кнопки доступными.
            productCategoryIco.setVisible(true); //Иконка продукта отображается (пока без логики).
            //TODO в зависимости от категории продукта или от продукта (это сложнее поддерживать в случае смены ассортимента),
            // иконка Продукта при выборе должна меняться.
            productNameLabel.setText(product.getProduct()); //Рядом с иконкой продукта отображается наименование продукта.
            //TODO в пользовательском интерфейсе сделать TextLabel с суммой около информации о выбранном продукте,
            // которая меняется при каждом совершаемом действии (выборе количества, процента скидки и т.д.)
            //Добавляем данные в currentProduct
            currentProduct = new SaleProduct(product.getProductId()
                    , product.getProduct()
                    , product.getPrice());
        }
    };
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/


    /*____________________________________start___________________________________________
     * Панель цифровых кнопок
     * Набор кнопок от 1 до 0 для использования при выборе количества продуктов.
     * Также содержит логику при нажатии на Цифровую кнопку.
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private GridPane numbersGridPane; //Цифровые кнопки, как в случае с кнопками Продуктов и Скидками, помещены в GridPane

    private ArrayList <Button> numberButtons = new ArrayList<>(); //Для этого создан ArrayList содержащий список объектов Button.
    //Непосредственное создание кнопок и распределение их по GridPane производится в методе initialization.

    EventHandler<ActionEvent> eventNumberButtons = new EventHandler<>() { //Действие, запускаемое при нажатии на одну из цифровых кнопок.
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            /*
             *При нажатии на Цифровую кнопку происходит обновление данных в amountLabel, отражающем выбранное количество Продукта
             * Также происходит пересчёт суммы Текущего продукта исходя из выбранного количества.
             * После чего Цифровые кнопки становятся недоступными, а кнопки с операциями над currentProduct становятся доступными.
             * TODO изменить логику. Возможно, при нажатии на 1 и потом снова на 1 количество должно становиться 11.
             *  Надо продумать этот момент. Или цифровые кнопки должны быть доступны до момента нажатия на "+" расположенной в Панели Текущего Продукта,
             *  который добавляет позицию в текущий Чек.
             */
            if (Integer.parseInt(button.getAccessibleText()) != 0) {
                endThisTale.setDisable(false); //Кнопка Чек становится доступна
                amountLabel.setText(button.getAccessibleText()); //Label количества берёт данные из AccessibleText цифровой кнопки.
                amountLabel.setVisible(true);
                currentProduct.setAmount(Integer.parseInt(button.getAccessibleText())); //для currentProduct устанавливается количество продукта.
                currentProduct.setSum(currentProduct.getPrice() * currentProduct.getAmount()); //сумма стоимости продукта исходя из выбранного количества.
                buttonsIsDisable(numberButtons, true);
                productOperationButtonsIsDisable(false);
            } else {
                /*
                 * Если гость передумал на данном моменте, нажатие на Цифровую кнопку 0,
                 * производит очистку объекта текущего продукта. Система возвращается в начальное состояние выбора продукта.
                 * При этом, если даже позиция в Чеке первая, новый чек не создаётся, т.к. в данном моменте значение переменной newSale типа boolean всё ещё false.
                 * (данное значение меняется на true только после формирования Чека при закрытии текущей продажи).
                 */
                currentProduct = null; //Текущий продукт становится null.
                productCategoryIco.setVisible(false); //Картинка Продукта перестаёт быть видимой
                xLabel.setVisible(false); //Символ количества перестаёт отображаться
                productNameLabel.setVisible(false); //Название продукта перестаёт отображаться
                amountLabel.setVisible(false); //Количество продукта перестаёт отображаться
                addProduct.setDisable(true); //Кнопка добавления продукта становится неактивной
                productOperationButtonsIsDisable(true); //Кнопки с операциями по текущему продукту становятся недоступными.
                buttonsIsDisable(numberButtons, true); //Цифровые кнопки становятся недоступными.
                buttonsIsDisable(productButtons,false); //Кнопки с Продуктами становятся активными.
            }
        }
    };
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
    private Button endThisTale; //сформировать чек.
    @FXML
    private Button endThisTaleAnother; //отменить чек.

    @FXML
    void discountButtonActivateOnAction() {
        //discountPanel, на которой находятся discountGrid, discountButtons становится видимой и доступной пользователю для дальнейших действий
        discountPanel.setVisible(true);
    }

    //На данный момент кнопка не имеет действия.
    @FXML
    void delProductOnAction() throws IOException, SQLException {
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
        saleTable.setItems(saleProductsObservableList);
        saleTable.refresh();

        //Подсчёт суммы продажи под таблицей текущей продажи.
        double total = 0.0;

        for (SaleProduct saleProduct : saleTable.getItems()) {
            total = total + saleProduct.getSum();
        }
        productCategoryIco.setVisible(false); //Картинка Продукта перестаёт быть видимой
        xLabel.setVisible(false); //Символ количества перестаёт отображаться
        sumLabel.setText(String.valueOf(total)); //Сумма устанавливается в sumLabel
        productNameLabel.setVisible(false); //Название продукта перестаёт отображаться
        amountLabel.setVisible(false); //Количество продукта перестаёт отображаться
        addProduct.setDisable(true); //Кнопка добавления продукта становится неактивной
        productOperationButtonsIsDisable(true);
        buttonsIsDisable(productButtons, false); //Кнопки с Продуктами становятся активными.
    }
    @FXML
    void endThisTaleOnAction() {
        paymentTypePanel.setVisible(true);
    }
    @FXML
    void endThisTaleAnotherOnAction() {
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

    private ArrayList<Button> discountButtons = new ArrayList<>();

    EventHandler<ActionEvent> eventDiscountButtons = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            Button button = (Button) event.getSource();
            discountPanel.setVisible(false); // Скрываем панель скидок
            currentProduct.setDiscountId(Integer.parseInt(button.getAccessibleText()));
            for (Discount discount : discounts) {
                if (currentProduct.getDiscountId() == discount.getDiscountId()) {
                    currentProduct.setDiscount(discount.getDiscount());
                }
            }
            currentProduct.setSum((currentProduct.getPrice() //Устанавливаем текущему продукту сумму исходя из количества и скидки
                    - (currentProduct.getPrice() * currentProduct.getDiscount() / 100))
                    * currentProduct.getAmount());
        }
    };
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель Чека (Выбор типа оплаты).
     * Данная панель открывается поверх панели Текущего товара нажатием на кнопку "Чек" находящуюся в панели
     * текущего товара.
     * //TODO Сделать логику выбора "нал/безнал". Нал - открывается панель с выбором "со сдачей/без сдачи".
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
    void oupsOnAction() throws IOException {
        correctionPane.setVisible(true);
//        clearLocalDb();
    }

    @FXML
    void paymentTypeOnAction(ActionEvent event) throws SQLException, IOException {
        Button button = (Button) event.getSource();
        currentSale.setPaymentTypeId(Integer.parseInt(button.getAccessibleText()));
        paymentTypePanel.setVisible(false);
        endThisTale.setDisable(true);
        long nowDate = System.currentTimeMillis();
        Date saleDate = new Date(nowDate);
        long nowTime = System.currentTimeMillis();
        Time saleTime = new Time(nowTime);
        currentSale.setCurrentDate(saleDate);
        currentSale.setCurrentTime(saleTime);
        currentSale.setClientId(1); //Временное назначение клиента

        if (Integer.parseInt(button.getAccessibleText()) == 1) {
            changePane.setVisible(true);
        } else if (Integer.parseInt(button.getAccessibleText()) == 2) {
            addSaleToLocalDb(currentSale);
//        createNewSale(currentSale);
            addProductsToLocalDb(currentSaleProducts, currentSale);
//        addProductsToSale(currentSaleProducts, currentSale);
            currentSale = null;
            newSale = true;
            positionsCount = 0;
            currentSaleProducts.clear();
            sumLabel.setText("0.00");
            //    userEarnings.setText(String.valueOf(reloadUserEarnings()));
            saleTable.refresh();
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
    void noChangeOnAction() throws IOException {
        addSaleToLocalDb(currentSale);
//        createNewSale(currentSale);
        addProductsToLocalDb(currentSaleProducts, currentSale);
//        addProductsToSale(currentSaleProducts, currentSale);
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        //    userEarnings.setText(String.valueOf(reloadUserEarnings()));
        saleTable.refresh();
        changePane.setVisible(false);
    }

    @FXML
    void withChangeOnAction() {
        changePane.setVisible(false);
        withChangePane.setVisible(true);
        System.out.println("With change pressed");
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
    private Button okWithChangeButton;

    @FXML
    void okWithChangeOnAction() throws IOException {
        addSaleToLocalDb(currentSale);
//        createNewSale(currentSale);
        addProductsToLocalDb(currentSaleProducts, currentSale);
//        addProductsToSale(currentSaleProducts, currentSale);
        currentSale = null;
        newSale = true;
        positionsCount = 0;
        currentSaleProducts.clear();
        sumLabel.setText("0.00");
        //    userEarnings.setText(String.valueOf(reloadUserEarnings()));
        saleTable.refresh();
        withChangePane.setVisible(false);
        System.out.println("okWithChangeOnAction");
    }

    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Панель корректировки
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    private AnchorPane correctionPane;

    @FXML
    private Button correctionButton;

    @FXML
    private Button endThisTaleAnother11;

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
    void endThisTaleAnother11OnAction() {
        correctionPane.setVisible(false);
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Инициализация
     _____________________________________˅˅˅____________________________________________*/
    @FXML
    void initialize() throws SQLException {
        createDiscountList();
        createPaymentTypeAL();
        createProductCategoriesList();
        System.out.println(productCategories.size());
        paymentTypePanel.setVisible(false);
        paymentTypeButtons[0] = paymentType1;
        paymentTypeButtons[1] = paymentType2;

        int count = 0;
        for (PaymentType paymentType : paymentTypes) {
            paymentTypeButtons[count].setAccessibleText(String.valueOf(paymentType.getPaymentTypeId()));
            paymentTypeButtons[count].setText(paymentType.getPaymentType());
            count++;
        }
        saleProductsObservableList.addListener((ListChangeListener<SaleProduct>) change -> {
        });

        for (int i = 0; i < numbersGridPane.getColumnCount(); i++) {
            Button numberButton = new Button();
            numberButtons.add(i, numberButton);
            int finalI = i;
            numberButtons.get(i).layoutBoundsProperty().addListener((observable, oldValue, newValue) -> numberButtons.get(finalI).setFont(Font.font(Math.sqrt(newValue.getHeight() * 10))));
            numberButtons.get(i).setWrapText(true);
            numberButtons.get(i).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
            numberButtons.get(i).setPrefSize(85.0, 85.0);
            numberButtons.get(i).setVisible(true);
            GridPane.setConstraints(numberButtons.get(i), i, 0);
            numbersGridPane.getChildren().add(numberButtons.get(i));
            numberButtons.get(i).setOnAction(eventNumberButtons);

            if (i < 9) {
                numberButtons.get(i).setText(String.valueOf(i + 1));
                numberButtons.get(i).setAccessibleText(String.valueOf(i + 1));
            } else {
                numberButtons.get(i).setText("0");
                numberButtons.get(i).setAccessibleText("0");
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
        buttonsIsDisable(numberButtons, true);
        endThisTale.setDisable(true);
        endThisTaleAnother.setDisable(true);

        //Размещаем кнопки с актуальными скидками.
        int countD = 0;
        for (int l = 0; l < discountGridPane.getColumnCount(); l++) {
            for (int h = 0; h < discountGridPane.getRowCount(); h++) {
                Button discountButton = new Button();
                discountButtons.add(countD, discountButton);
                int finalDiscountButtonsCount = countD;
                discountButtons.get(countD).layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
                        discountButtons.get(finalDiscountButtonsCount).setFont(
                                Font.font(Math.sqrt(newValue.getHeight() * 1.5))));
                discountButtons.get(countD).setWrapText(true);
                discountButtons.get(countD).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
                discountButtons.get(countD).setPrefSize(75.0, 75.0);
                discountButtons.get(countD).setVisible(false);
                GridPane.setConstraints(discountButtons.get(countD), l, h);
                discountGridPane.getChildren().add(discountButtons.get(countD));
                discountButtons.get(countD).setOnAction(eventDiscountButtons);
                countD++;
            }
        }
        discountNameButtons(discountButtons);
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
                productButtons.add(countP, productButton);
                int finalProdButtonsCount = countP;
                productButtons.get(countP).layoutBoundsProperty().addListener((observable, oldValue, newValue) ->
                        productButtons.get(finalProdButtonsCount).setFont(
                                Font.font(Math.sqrt(newValue.getHeight() * 1.5))));
                productButtons.get(countP).setWrapText(true);
                productButtons.get(countP).setStyle("-fx-text-alignment: CENTER; -fx-font-weight: BOLDER");
                productButtons.get(countP).setPrefSize(85.0, 85.0);
                productButtons.get(countP).setVisible(false);
                GridPane.setConstraints(productButtons.get(countP), l, h);
                mainGridPane.getChildren().add(productButtons.get(countP));
                productButtons.get(countP).setOnAction(eventProductButtons);
                countP++;
            }
        }
        productNameButton(productButtons);
    }
    /*____________________________________˄˄˄_____________________________________________
     ___________________________________the end__________________________________________*/

    /*____________________________________start___________________________________________
     * Прочие методы.
     _____________________________________˅˅˅____________________________________________*/
    /**
     * Данный метод делает кнопки с Продуктами/Цифровые кнопки доступными/недоступными.
     * @param buttons - принимаемый параметр - массив кнопок.
     * @param res - значение boolean отражающее действие, которое необходимо совершить с кнопками.
     */
    public void buttonsIsDisable(ArrayList<Button> buttons, boolean res) {
        for (Button button : buttons) {
            button.setDisable(res);
        }
    }

    /**
     * Данный метод делает кнопки с действиями с Добавляемым продуктом активными/неактивными
     * Например: кнопки Работы с Выбранным продуктом недоступны пока не выбран продукт.
     * @param res - тип boolean, который работает как переключатель доступности.
     */
    public void productOperationButtonsIsDisable(boolean res) {
        addProduct.setDisable(res);
        discountButtonActivate.setDisable(res);
        delProduct.setDisable(res);
    }
}