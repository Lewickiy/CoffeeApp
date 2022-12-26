package com.lewickiy.coffeeboardapp.dao.sync;

import static com.lewickiy.coffeeboardapp.dao.info.SyncInfo.getInfoMessage;
import static com.lewickiy.coffeeboardapp.dao.query.ShiftLog.syncShiftLog;
import static com.lewickiy.coffeeboardapp.dao.query.SyncCorrected.syncCorrectedSales;
import static com.lewickiy.coffeeboardapp.dao.query.SyncProductSales.syncSalesProduct;
import static com.lewickiy.coffeeboardapp.dao.query.SyncSales.syncSales;

public class ShiftData {
    /**
     * Метод запускающий синхронизацию:<br>
     * - сообщений от администратора<br>
     * - откорректированных продаж<br>
     * - лога открытия/закрытия смены<br>
     * - чеков<br>
     * - продуктов в чеках<br>
     */
    public static void syncShiftData() {
        Thread periodicSyncThread = new Thread(() -> {
            getInfoMessage(); //Сообщения от администратора
            syncCorrectedSales(); //Откорректированные продажи
            syncShiftLog(); //Лог открытия/закрытия смены
            syncSales(); //Чеки
            syncSalesProduct(); //Продукты в чеках
        });
        periodicSyncThread.setDaemon(true);
        periodicSyncThread.start();
    }
}