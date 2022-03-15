package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class JDBC_WorldLock implements WorldLock{
    JDBC_DatasheetManger datasheetManger;
    UUID worldsUUID;
    String lockServerName;

    public JDBC_WorldLock(JDBC_DatasheetManger datasheetManger, UUID worldsUUID, String lockServerName) {
        this.datasheetManger = datasheetManger;
        this.worldsUUID = worldsUUID;
        this.lockServerName = lockServerName;
    }

    @Override
    public void unLock() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update Worlds set LockServerName=? where WorldsUUID=? and LockServerName=?")){
                preparedStatement.setBytes(1,null);
                preparedStatement.setString(2,worldsUUID.toString());
                preparedStatement.setString(3,lockServerName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
