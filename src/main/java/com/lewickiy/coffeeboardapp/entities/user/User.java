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
    /**
     * Второй конструктор, с меньшим числом принимаемых параметров. Отличается от полного конструктора <br>
     * отсутствием параметров Отчества и даты рождения.
     * @param id - id пользователя из базы SQL
     * @param login - логин пользователя из базы SQL
     * @param firstName - имя пользователя. Будет использоваться для обращения системы к пользователю
     * @param lastName  - фамилия пользователя.
     * @param phone - контактный номер телефона пользователя
     * @param administrator - значение boolean. Является ли пользователь администратором системы для выполнения "особых" операций (false/true)
     * @param activeStuff - значение boolean. Является ли сотрудник действующим (false/true)
     */
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