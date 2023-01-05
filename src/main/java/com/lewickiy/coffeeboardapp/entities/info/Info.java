package com.lewickiy.coffeeboardapp.entities.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Info {
    public static boolean newMessage = false;
    public static Info info;
    long infoId;
    int outletId;
    String message;
    Boolean delivered;

    public static boolean isNewMessage() {
        return newMessage;
    }

    public static void setNewMessage(boolean newMessage) {
        Info.newMessage = newMessage;
    }

    @Override
    public String toString() {
        return infoId + " - info id, " + outletId + " - outlet id, " + message + " - message.";
    }
}