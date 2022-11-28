package com.lewickiy.coffeeboardapp.database.user;

import java.util.Date;

public class User {
    private int userId;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Date birthday;
    private String phone;
    private boolean administrator;
    private boolean activeStuff;
    public User() {

    }
    public User(int userId
            , String login
            , String password
            , String firstName
            , String lastName
            , String patronymic
            , Date birthday
            , String phone
            , boolean administrator
            , boolean activeStuff) {

        this.userId = userId;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.birthday = birthday;
        this.phone = phone;
        this.administrator = administrator;
        this.activeStuff = activeStuff;
    }
    /**
     * Второй конструктор, с меньшим числом принимаемых параметров. Отличается от полного конструктора <br>
     * отсутствием параметров Отчества и даты рождения.
     * @param userId - id пользователя из базы SQL
     * @param login - логин пользователя из базы SQL
     * @param firstName - имя пользователя. Будет использоваться для обращения системы к пользователю
     * @param lastName  - фамилия пользователя.
     * @param phone - контактный номер телефона пользователя
     * @param administrator - значение boolean. Является ли пользователь администратором системы для выполнения "особых" операций (false/true)
     * @param activeStuff - значение boolean. Является ли сотрудник действующим (false/true)
     */
    public User(int userId
            , String login
            , String password
            , String firstName
            , String lastName
            , String phone
            , boolean administrator
            , boolean activeStuff) {

        this.userId = userId;
        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.administrator = administrator;
        this.activeStuff = activeStuff;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }



    public boolean isActiveStuff() {
        return activeStuff;
    }

    public void setActiveStuff(boolean activeStuff) {
        this.activeStuff = activeStuff;
    }
}