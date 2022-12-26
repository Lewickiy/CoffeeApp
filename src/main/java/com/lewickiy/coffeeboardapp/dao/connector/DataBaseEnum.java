package com.lewickiy.coffeeboardapp.dao.connector;

public enum DataBaseEnum {
    NETWORK_DB("network_database"),
    LOCAL_DB("local_database");

    private final String dbName;
    DataBaseEnum(String dbName) {
        this.dbName = dbName;
    }
    public String getDbName() {
        return dbName;
    }
}
