package com.lewickiy.coffeeboardapp.controllers.seller.actions;

import com.lewickiy.coffeeboardapp.entities.product.Product;
import com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategory;
import javafx.scene.control.Button;

import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.entities.productcategory.ProductCategoryList.productCategories;
import static com.lewickiy.coffeeboardapp.entities.product.ProductList.products;
/**
 * Данный класс занимается именованием кнопок с продукцией. Если появится такая необходимость, <br>
 * существует возможность добавлять данные к именам кнопок, например: стоимость товара или любые <br>
 * иные данные о продукции из базы данных.
 */
public class ProductNameButton {
    /**
     * Данный метод непосредственно занимается именованием кнопок с продукцией в классе SellerController<br>
     * он принимает в себя параметр (массив кнопок), далее, итерацией проходится по ProductList,<br>
     * параллельно присваивая кнопкам имена button[].setText..., а также устанавливая AccessibleText<br>
     * Этот текст является product_id of product и, в дальнейшем, планируется для осуществления операций с продуктами<br>
     * при нажатии на кнопку.<br>
     * Этим предполагается будет заниматься класс<br>
     * Также данный метод делает кнопки доступными (productButtons[].setDisable())<br>
     * @param productButtons - это массив кнопок, созданных в SellerController.
     */
    public static void productNameButton(ArrayList<Button> productButtons) {
        int count = 0;
        int temp = 0;
        for (ProductCategory productCategory : productCategories) {
            double amountCol = Math.ceil(productCategory.getAmountProducts() / 5.0);
            temp = temp + (int)amountCol * 5;
            for (Product product : products) {
                if (productCategory.getProductCategoryId() == product.getCategory()) {
                    productButtons.get(count).setAccessibleText(String.valueOf(product.getProductId()));
                    productButtons.get(count).setText(product.getProduct()
                            + "\n"
                            + product.getNumberOfUnit()
                            + " "
                            + product.getUnitOfMeasurement()
                            + "\n"
                            + product.getPrice()
                            + "руб.");
                    productButtons.get(count).setVisible(true);
                    productButtons.get(count).setDisable(false);
                    count++;
                }
            }
            count = temp;
        }
    }
}