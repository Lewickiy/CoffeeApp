package com.lewickiy.coffeeboardapp.dao.sync;

import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.syncOutletsList;
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.syncUsersList;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.createOutletList;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.createUsersList;

public class StartData {
    /**
     * Синхронизация баз данных при запуске LoginController
     */
    public static void syncStartData() {
        syncUsersList();
        syncOutletsList();

        createUsersList();
        createOutletList();
    }
}