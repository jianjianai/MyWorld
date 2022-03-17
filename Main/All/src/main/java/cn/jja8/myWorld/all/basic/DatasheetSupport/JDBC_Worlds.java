package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JDBC_Worlds implements Worlds{
    JDBC_DatasheetManger datasheetManger;
    UUID worldsUUID;
    public JDBC_Worlds(JDBC_DatasheetManger datasheetManger, UUID worldsUUID) {
        this.datasheetManger = datasheetManger;
        this.worldsUUID = worldsUUID;
    }

    @Override
    public List<String> getWorldList() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select WorldName from World where WorldsUUID=?")){
                preparedStatement.setString(1, worldsUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    ArrayList<String> worldList = new ArrayList<>();
                    while (resultSet.next()){
                        worldList.add(resultSet.getString(1));
                    }
                    return worldList;
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public void putWorld(String worldName) {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into World(WorldsUUID,WorldName) values(?,?)")){
                preparedStatement.setString(1, worldsUUID.toString());
                preparedStatement.setString(2, worldName);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public String getWorldsName() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select WorldsName from Worlds where WorldsUUID=?")){
                preparedStatement.setString(1, worldsUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        return resultSet.getString(1);
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public UUID getUUID() {
        return worldsUUID;
    }

    @Override
    public void delete() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from Worlds where WorldsUUID=?")){
                preparedStatement.setString(1,worldsUUID.toString());
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from World where WorldsUUID=?")){
                preparedStatement.setString(1,worldsUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public Team getTeam() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select UUID from Team where WorldsUUID=?")){
                preparedStatement.setString(1,worldsUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        return new JDBC_Team(datasheetManger,UUID.fromString(resultSet.getString(1)));
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public WorldsData getWorldsData(String dataName) {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select WorldsUUID,DataName from WorldsData where WorldsUUID=? and DataName=?")){
                preparedStatement.setString(1,worldsUUID.toString());
                preparedStatement.setString(2,dataName);
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        return new JDBC_WorldsData(datasheetManger,resultSet.getString("WorldsUUID"),resultSet.getString("DataName"));
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public WorldsData newWorldsData(String dataName) {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into WorldsData(WorldsUUID,DataName) values(?,?)")){
                preparedStatement.setString(1,worldsUUID.toString());
                preparedStatement.setString(2,dataName);
                int up = preparedStatement.executeUpdate();
                if (up==1){
                    return new JDBC_WorldsData(datasheetManger,worldsUUID.toString(),dataName);
                }
            }
        } catch (SQLException sqlException) {
            if (sqlException.getErrorCode()==19){
                return null;
            }
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public WorldLock getWorldLock(String lockServerName) {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update Worlds set LockServerName=? where WorldsUUID=? and LockServerName=?")){
                preparedStatement.setString(1,lockServerName);
                preparedStatement.setString(2,worldsUUID.toString());
                preparedStatement.setString(3,null);
                int i = preparedStatement.executeUpdate();
                if (i>0){
                    return new JDBC_WorldLock(datasheetManger,worldsUUID,lockServerName);
                }else {
                    return null;
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public String getLockServerName() {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select LockServerName from Worlds where WorldsUUID=?")){
                preparedStatement.setString(1,worldsUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        return resultSet.getString(1);
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public void unAllLock(String lockServerName) {
        try (Connection connection = datasheetManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update Worlds set LockServerName=? where LockServerName=?")){
                preparedStatement.setString(1,null);
                preparedStatement.setString(2,lockServerName);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
