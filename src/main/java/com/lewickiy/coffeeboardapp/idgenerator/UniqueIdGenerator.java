package com.lewickiy.coffeeboardapp.idgenerator;

import java.util.UUID;

public class UniqueIdGenerator {
    public static int getId() {
        String id;
        UUID uuid = UUID.randomUUID();
        id = uuid.toString();
        id = id.replace("-", "");
        int num = id.hashCode();
        num = num < 0 ? -num : num;
        return num;
    }
}