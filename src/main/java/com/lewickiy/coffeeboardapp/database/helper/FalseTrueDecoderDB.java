package com.lewickiy.coffeeboardapp.database.helper;

import java.util.logging.Level;

import static com.lewickiy.coffeeboardapp.CoffeeBoardApp.LOGGER;

public class FalseTrueDecoderDB {
    /**
     * Декодер для boolean в базах данных при использовании в синхронизации между sql и sqlite
     * @param incomingInt - в формате int от базы данных 0/1
     * @return boolean значение false/true.
     */
    public static boolean decodeIntBoolean(int incomingInt) {
        boolean decoded;
        if (incomingInt == 1) {
            decoded = true;
        } else if (incomingInt == 0) {
            decoded = false;
        } else {
            LOGGER.log(Level.WARNING,"Method takes incoming data not 0 or 1.");
            decoded = false;
        }
        return decoded;
    }
    /**
     * Декодер для boolean в базах данных при использовании в синхронизации между sql и sqlite
     * @param incomingBoolean - в формате boolean от базы данных false/true
     * @return int значение 0/1.
     */
    public static int decodeIntBoolean(boolean incomingBoolean) {
        int decoded;
        if (incomingBoolean) {
            decoded = 1;
        } else {
            decoded = 0;
        }
        return decoded;
    }
}
