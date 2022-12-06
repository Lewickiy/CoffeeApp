package com.lewickiy.coffeeboardapp.idgenerator;

import java.sql.Date;
import java.sql.Time;

import static com.lewickiy.coffeeboardapp.entities.outlet.Outlet.currentOutlet;
import static com.lewickiy.coffeeboardapp.entities.user.UserList.currentUser;

public class UniqueIdGenerator {
    /**
     * The method generates a receipt number in the following format:<br>
     * 20221204 (date) + 145522 (time) + 5 (point of sale ID) + 2 (user ID)<br>
     * = 2022120414552252.<br>
     * @return - long receipt ID.
     */
    public static long getId() {
        String date;
        String time;
        long resLong;
        long nowDateLong = System.currentTimeMillis();
        Date nowDate = new Date(nowDateLong);
        long nowTimeLong = System.currentTimeMillis();
        Time nowTime = new Time(nowTimeLong);
        date = nowDate.toString().replace("-", "");
        time = nowTime.toString().replace(":", "");
        resLong = Long.parseLong(date + time + currentOutlet.getOutletId() + currentUser.getUserId());
        return resLong;
    }
}