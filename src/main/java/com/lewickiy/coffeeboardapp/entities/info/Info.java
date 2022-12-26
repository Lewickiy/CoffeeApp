package com.lewickiy.coffeeboardapp.entities.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Info {
    public static Info info;
    long infoId;
    int outletId;
    String message;
    Boolean delivered;
}