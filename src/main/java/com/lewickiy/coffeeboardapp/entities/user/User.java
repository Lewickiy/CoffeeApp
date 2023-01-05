package com.lewickiy.coffeeboardapp.entities.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private int id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Date birthday;
    private String phone;
    private boolean administrator;
    private boolean activeStuff;
    public User(int id
            , String login
            , String password
            , String firstName
            , String lastName
            , String phone
            , boolean administrator
            , boolean activeStuff) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.administrator = administrator;
        this.activeStuff = activeStuff;
    }
}