package com.lewickiy.coffeeboardapp.database.local;

import com.lewickiy.coffeeboardapp.database.currentSale.CurrentSale;
import com.lewickiy.coffeeboardapp.database.currentSale.SaleProduct;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.insertToSql;

public class LocalBase {
    private static File localDb;

    public static void createLocalDb() throws IOException {
        localDb = new File("local_db.xlsx");
        if (localDb.exists()) {
            System.out.println("File exist");
        } else {
            XSSFWorkbook local_db = new XSSFWorkbook();
            XSSFSheet sale = local_db.createSheet("sale");
            XSSFRow row;
            XSSFCell cell;

            row = sale.createRow(0);

            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue("sale_id");
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue("user_id");
            cell = row.createCell(2, CellType.NUMERIC);
            cell.setCellValue("outlet_id");
            cell = row.createCell(3,CellType.STRING);
            cell.setCellValue("currentDate");
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue("currentTime");
            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue("paymentType_id");
            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue("client_id");

            XSSFSheet saleProduct = local_db.createSheet("sale_product");
            row = saleProduct.createRow(0);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue("sale_id");
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue("product_id");
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue("product");
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue("price");
            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue("discount_id");
            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue("discount");
            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue("amount");
            cell = row.createCell(7, CellType.STRING);
            cell.setCellValue("sum");

            for (int i = 0; i < 8; i++) {
                sale.autoSizeColumn(i);
                saleProduct.autoSizeColumn(i);
            }

            FileOutputStream out = new FileOutputStream("local_db.xlsx");
            {
                try {
                    local_db.write(out);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("local_db created");

            }
        }
    }

    /**
     * Добавление Чека в локальную базу данных.
     * @param currentSale - текущая продажа
     * @throws IOException - ...
     */
    public static void addSaleToLocalDb(CurrentSale currentSale) throws IOException {

        XSSFRow row;
        XSSFCell cell;

        InputStream myxlsx = new FileInputStream("local_db.xlsx");
        XSSFWorkbook book = new XSSFWorkbook(myxlsx);
        XSSFSheet sale = book.getSheet("sale");
        int nextRow = sale.getLastRowNum() + 1;

        row = sale.createRow(nextRow);
        cell = row.createCell(0, CellType.NUMERIC);
        cell.setCellValue(currentSale.getSaleId());
        cell = row.createCell(1, CellType.NUMERIC);
        cell.setCellValue(currentSale.getUserId());
        cell = row.createCell(2, CellType.NUMERIC);
        cell.setCellValue(currentSale.getSaleOutletId());
        cell = row.createCell(3,CellType.STRING);
        cell.setCellValue(currentSale.getCurrentDate().toString());
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue(currentSale.getCurrentTime().toString());
        cell = row.createCell(5, CellType.NUMERIC);
        cell.setCellValue(currentSale.getPaymentTypeId());
        cell = row.createCell(6, CellType.NUMERIC);
        cell.setCellValue(currentSale.getClientId());

        for (int i = 0; i < 8; i++) {
            sale.autoSizeColumn(i);
        }
        FileOutputStream os = new FileOutputStream("local_db.xlsx");
        book.write(os);
    }

    /**
     * Добавление продуктов в локальную базу данных
     * @param currentSaleProducts - принимает список продуктов
     * @param currentSale - текущую продажу (Чек)
     * @throws IOException -...
     */
    public static void addProductsToLocalDb(ArrayList<SaleProduct> currentSaleProducts, CurrentSale currentSale) throws IOException {
        XSSFRow row;
        XSSFCell cell;

        InputStream myxlsx = new FileInputStream("local_db.xlsx");
        XSSFWorkbook book = new XSSFWorkbook(myxlsx);
        XSSFSheet saleProduct = book.getSheet("sale_product");

        for (SaleProduct currentSaleProduct : currentSaleProducts) {
            row = saleProduct.createRow(saleProduct.getLastRowNum() + 1);
            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(currentSale.getSaleId());
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue(currentSaleProduct.getProductId());
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(currentSaleProduct.getProduct());
            cell = row.createCell(3, CellType.NUMERIC);
            cell.setCellValue(currentSaleProduct.getPrice());
            cell = row.createCell(4, CellType.NUMERIC);
            cell.setCellValue(currentSaleProduct.getDiscountId());
            cell = row.createCell(5, CellType.NUMERIC);
            cell.setCellValue(currentSaleProduct.getDiscount());
            cell = row.createCell(6, CellType.NUMERIC);
            cell.setCellValue(currentSaleProduct.getAmount());
            cell = row.createCell(7, CellType.NUMERIC);
            cell.setCellValue(currentSaleProduct.getSum());

            for (int i = 0; i < 8; i++) {
                saleProduct.autoSizeColumn(i);
            }
        }

        FileOutputStream os = new FileOutputStream("local_db.xlsx");
        book.write(os);
    }

    /**
     * Очистка локальной базы данных.
     * Должна выполняться после переноса данных в
     * @throws IOException - ...
     */
    public static void clearLocalDb() throws IOException {
        InputStream myxlsx = new FileInputStream("local_db.xlsx");
        XSSFWorkbook book = new XSSFWorkbook(myxlsx);
        XSSFSheet sale = book.getSheet("sale");
        XSSFSheet saleProduct = book.getSheet("sale_product");

        for(int i = sale.getLastRowNum(); i > 0; i--) {
            sale.removeRow(sale.getRow(i));
        }
        for (int i = saleProduct.getLastRowNum(); i > 0; i--) {
            saleProduct.removeRow(saleProduct.getRow(i));
        }

        FileOutputStream os = new FileOutputStream("local_db.xlsx");
        book.write(os);
    }

    public static void writeSqlFromLocalDb() throws IOException, SQLException {

        InputStream myxlsx = new FileInputStream("local_db.xlsx");
        XSSFWorkbook book = new XSSFWorkbook(myxlsx);
        XSSFSheet sale = book.getSheet("sale");

        int lastRowS = sale.getLastRowNum();
        for (int i = 1; i <= lastRowS; i++) {
            int saleId = (int) sale.getRow(i).getCell(0).getNumericCellValue();
            System.out.println("Sale id: " + saleId);
            int userId = (int) sale.getRow(i).getCell(1).getNumericCellValue();
            System.out.println("User id: " + userId);
            int outletId = (int) sale.getRow(i).getCell(2).getNumericCellValue();
            System.out.println("Outlet id: " + outletId);
            Date currentDate = Date.valueOf(sale.getRow(i).getCell(3).getStringCellValue());
            System.out.println("Current date: " + currentDate);
            Time currentTime = Time.valueOf(sale.getRow(i).getCell(4).getStringCellValue());
            System.out.println("Current time: " + currentTime);
            int paymentTypeId = (int) sale.getRow(i).getCell(5).getNumericCellValue();
            System.out.println("Payment type id: " + paymentTypeId);
            int clientId = (int) sale.getRow(i).getCell(6).getNumericCellValue();
            System.out.println("Client Id: " + clientId);
            CurrentSale tempSale = new CurrentSale(saleId, userId, outletId, currentDate, currentTime, paymentTypeId, clientId);
            System.out.println("__________________________________________________________________________");
            System.out.println();

            insertToSql("sale", "sale_id, "
                    + "user_id, "
                    + "outlet_id, "
                    + "date, "
                    + "time, "
                    + "paymenttype_id, "
                    + "client_id) VALUES ('"
                    + tempSale.getSaleId() + "', '"
                    + tempSale.getUserId() + "', '"
                    + tempSale.getSaleOutletId() + "', '"
                    + tempSale.getCurrentDate() + "', '"
                    + tempSale.getCurrentTime() + "', '"
                    + tempSale.getPaymentTypeId() + "', '"
                    + tempSale.getClientId());
        }

        XSSFSheet saleProduct = book.getSheet("sale_product");

        int lastRowSP = saleProduct.getLastRowNum();
        for (int i = 1; i <= lastRowSP; i++) {
            int saleId = (int) saleProduct.getRow(i).getCell(0).getNumericCellValue();
            System.out.println("Sale id: " + saleId);
            int productId = (int) saleProduct.getRow(i).getCell(1).getNumericCellValue();
            System.out.println("Product id: " + productId);
            String product = String.valueOf(saleProduct.getRow(i).getCell(2));
            System.out.println("Product: " + product);
            double price = saleProduct.getRow(i).getCell(3).getNumericCellValue();
            System.out.println("Price: " + price);
            int discountId = (int) saleProduct.getRow(i).getCell(4).getNumericCellValue();
            System.out.println("Discount id: " + discountId);
            int discount = (int) saleProduct.getRow(i).getCell(5).getNumericCellValue();
            System.out.println("Discount: " + discount);
            int amount = (int) saleProduct.getRow(i).getCell(6).getNumericCellValue();
            System.out.println("Amount: " + amount);
            double sum = saleProduct.getRow(i).getCell(7).getNumericCellValue();
            System.out.println("Sum: " + sum);
            SaleProduct tempSaleProduct = new SaleProduct(saleId, productId, product, price, discountId, discount, amount, sum);
            System.out.println("__________________________________________________________________________");
            System.out.println();

            insertToSql("sale_product", "sale_id, "
                    + "product_id, "
                    + "discount_id, "
                    + "price, "
                    + "amount, "
                    + "sum) VALUES ('"
                    + tempSaleProduct.getSaleId() + "', '"
                    + tempSaleProduct.getProductId() + "', '"
                    + tempSaleProduct.getDiscountId() + "', '"
                    + tempSaleProduct.getPrice() + "', '"
                    + tempSaleProduct.getAmount() + "', '"
                    + tempSaleProduct.getSum());
        }
        clearLocalDb();
    }
}