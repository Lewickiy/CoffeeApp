package com.lewickiy.coffeeboardapp.dao.sync;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.syncOutletsList;
import static com.lewickiy.coffeeboardapp.dao.SyncLocalDB.syncUsersList;
import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.connector.NDBConnector.getConnectionNDB;
import static com.lewickiy.coffeeboardapp.entities.outlet.OutletList.createOutletList;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.createUsersList;

public class StartData {
    /**
     * Синхронизация баз данных при запуске LoginController
     * @throws SQLException - ...
     * @throws ParseException - ...
     */
    public static void syncStartData() throws SQLException, ParseException {
        Connection conNDB = getConnectionNDB();
        Connection conLDB = getConnectionLDB();
        if (conNDB != null) {
            syncUsersList(conNDB);
            syncOutletsList(conNDB);

            createUsersList(conLDB);
            createOutletList(conLDB);
            conNDB.close();

            if(conLDB != null) {
                conLDB.close();
            }
            LOGGER.log(Level.FINE, "Synchronization completed successfully");
        } else {
            LOGGER.log(Level.WARNING, "When starting to synchronize with the network database at Login startup, it was not possible to connect to the network. Data is taken from local database");
            createUsersList(conLDB);
            createOutletList(conLDB);
            if (conLDB != null) {
                conLDB.close();
            }
            LOGGER.log(Level.INFO, "Further work may be carried out with outdated data");
        }
    }
}