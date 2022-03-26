package cn.jja8.myWorld.all.basic.DatasheetSupport;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JDBC_TeamPlayer implements TeamPlayer {
    JDBC_DatasheetManger teamManger;
    UUID PlayerUUID;
    public JDBC_TeamPlayer(JDBC_DatasheetManger teamManger, UUID playerUUID) {
        this.teamManger = teamManger;
        PlayerUUID = playerUUID;
    }

    @Override
    public UUID getPlayerUUID() {
        return PlayerUUID;
    }

    @Override
    public Status getStatus() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select Status from TeamPlayer where PlayerUUID=?")){
                preparedStatement.setString(1,PlayerUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        String sta = resultSet.getString(1);
                        if (sta!=null){
                            try {
                                return Status.valueOf(sta);
                            }catch (IllegalArgumentException illegalArgumentException){
                                illegalArgumentException.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return Status.player;
    }

    @Override
    public void setTeam(Team team) {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update TeamPlayer set TeamUUID=? where PlayerUUID=?")){
                preparedStatement.setString(1,team==null?null:team.getUUID().toString());
                preparedStatement.setString(2,PlayerUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public Team getTeam() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select TeamUUID from TeamPlayer where PlayerUUID=?")){
                preparedStatement.setString(1,PlayerUUID.toString());
                try (ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        String sta = resultSet.getString(1);
                        if (sta!=null){
                            try {
                                return new JDBC_Team(teamManger,UUID.fromString(sta));
                            }catch (IllegalArgumentException illegalArgumentException){
                                illegalArgumentException.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public void setStatus(Status status) {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update TeamPlayer set Status=? where PlayerUUID=?")){
                preparedStatement.setString(1,status==null?null:status.toString());
                preparedStatement.setString(2,PlayerUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public String getName() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select PlayerName from TeamPlayer where PlayerUUID=?")){
                preparedStatement.setString(1,PlayerUUID.toString());
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
    public void setName(String name) {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update TeamPlayer set PlayerName=? where PlayerUUID=?")){
                preparedStatement.setString(1,name);
                preparedStatement.setString(2,PlayerUUID.toString());
                preparedStatement.executeQuery();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void delete() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from TeamPlayer where PlayerUUID=?")){
                preparedStatement.setString(1,PlayerUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return getName();
    }


    @Override
    public int hashCode() {
        return PlayerUUID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JDBC_TeamPlayer) {
            return PlayerUUID.equals(((JDBC_TeamPlayer)obj).PlayerUUID);
        }
        return false;

    }
}
