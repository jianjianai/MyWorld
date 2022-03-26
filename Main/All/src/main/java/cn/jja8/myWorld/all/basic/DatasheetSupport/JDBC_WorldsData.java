package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.sql.*;

public class JDBC_WorldsData implements WorldsData{
    JDBC_DatasheetManger datasheetManger;
    String worldsUUID, dataName;

    public JDBC_WorldsData(JDBC_DatasheetManger datasheetManger, String worldsUUID, String dataName) {
        this.datasheetManger = datasheetManger;
        this.worldsUUID = worldsUUID;
        this.dataName = dataName;
    }

    @Override
    public byte[] getData() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select Data from WorldsData where WorldsUUID=? and DataName=?")){
                preparedStatement.setString(1, worldsUUID);
                preparedStatement.setString(2, dataName);
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        return resultSet.getBytes(1);
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public void setData(byte[] newData) {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update WorldsData set Data=? where WorldsUUID=? and DataName=?")){
                preparedStatement.setBytes(1,newData);
                preparedStatement.setString(2,worldsUUID);
                preparedStatement.setString(3,dataName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return (worldsUUID+dataName).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JDBC_WorldsData) {
            JDBC_WorldsData i = (JDBC_WorldsData)obj;
            return worldsUUID.equals(i.worldsUUID)&&dataName.equals(i.worldsUUID);
        }
        return false;
    }
}
