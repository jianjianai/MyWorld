package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC_WorldLock implements WorldLock{
    Connection lockConnection;
    ResultSet lockResultSet;

    public JDBC_WorldLock(Connection lockConnection, ResultSet lockResultSet) {
        this.lockConnection = lockConnection;
        this.lockResultSet = lockResultSet;
    }

    @Override
    public void unLock() {
        try {
            lockResultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            lockConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
