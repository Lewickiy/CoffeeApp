package com.lewickiy.coffeeboardapp.entities.outlet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.lewickiy.coffeeboardapp.dao.connector.LDBConnector.getConnectionLDB;
import static com.lewickiy.coffeeboardapp.dao.query.Query.selectAllFromSql;

public class OutletList {
    public static List<Outlet> outlets = new ArrayList<>();
    /**
     * Метод загружающий перечень торговых точек в ArrayList outlets, для дальнейшей работы с ними <br>
     * при формировании чеков и различных отчётов.
     */
    public static void createOutletList() {
        Connection con;
        ResultSet resultSet;
        try {
            con = getConnectionLDB();
            if (con != null) {
                resultSet = selectAllFromSql(con, "local_database","outlet");

                while(resultSet.next()) {
                    int outletId = resultSet.getInt("outlet_id");
                    String outlet = resultSet.getString("outlet");
                    outlets.add(new Outlet(outletId, outlet));
                }
                resultSet.close();
                con.close();
            } else {
                //TODO log
            }
        } catch (SQLException sqlEx) {
            //TODO log
        }
    }
}