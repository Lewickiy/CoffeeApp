package com.lewickiy.coffeeboardapp.database.outlet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.lewickiy.coffeeboardapp.database.Query.selectAllFromSql;

public class OutletList {
    public static ArrayList<Outlet> outlets = new ArrayList<>();
    /**
     * Метод загружающий перечень торговых точек в ArrayList outlets, для дальнейшей работы с ними <br>
     * при формировании чеков и различных отчётов.
     * @throws SQLException - не обработано
     */
    public static void createOutletList() throws SQLException {
        ResultSet resultSet = selectAllFromSql("local_database","outlet");
        while(resultSet.next()) {
            int outletId = resultSet.getInt("outlet_id");
            String outlet = resultSet.getString("outlet");
            outlets.add(new Outlet(outletId, outlet));
        }
    }
}