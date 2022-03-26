package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JDBC_Team implements Team {
    JDBC_DatasheetManger teamManger;
    UUID TeamUUID;

    JDBC_Team(JDBC_DatasheetManger teamManger, UUID teamUUID) {
        this.teamManger = teamManger;
        TeamUUID = teamUUID;
    }

    @Override
    public String getTeamName() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select TeamName from Team where UUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
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
    public void delete() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from Team where UUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from TeamPlayer where TeamUUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public List<TeamPlayer> getPlayers(Status status) {
        try (Connection connection = teamManger.getConnection()){
            ArrayList<TeamPlayer> playerList = new ArrayList<>();
            if (status==null){
                try (PreparedStatement preparedStatement = connection.prepareStatement("select PlayerUUID from TeamPlayer where TeamUUID=?")){
                    preparedStatement.setString(1,TeamUUID.toString());
                    try (ResultSet resultSet = preparedStatement.executeQuery();){

                        while (resultSet.next()){
                            playerList.add(new JDBC_TeamPlayer(teamManger,UUID.fromString(resultSet.getString(1))));
                        }
                    }
                }
            }else {
                try (PreparedStatement preparedStatement = connection.prepareStatement("select PlayerUUID from TeamPlayer where Status=? and TeamUUID=?")){
                    preparedStatement.setString(1,status.toString());
                    preparedStatement.setString(2,TeamUUID.toString());
                    try (ResultSet resultSet = preparedStatement.executeQuery();){
                        while (resultSet.next()){
                            playerList.add(new JDBC_TeamPlayer(teamManger,UUID.fromString(resultSet.getString(1))));
                        }
                    }
                }
            }
            return playerList;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    @Override
    public UUID getUUID() {
        return TeamUUID;
    }

    @Override
    public void setTeamName(String teamName) {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update Team set TeamName=? where UUID=?")){
                preparedStatement.setString(1,teamName);
                preparedStatement.setString(2,TeamUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public Worlds getWorlds() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select WorldsUUID from Team where UUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
                try ( ResultSet resultSet = preparedStatement.executeQuery();){
                    if (resultSet.next()){
                        String uu = resultSet.getString(1);
                        if (uu!=null) {
                            return new JDBC_Worlds(teamManger,UUID.fromString(uu));
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
    public void setWorlds(Worlds worlds) {
        UUID worldsUUID = worlds.getUUID();
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update Team set WorldsUUID=? where UUID=?")){
                preparedStatement.setString(1,worldsUUID.toString());
                preparedStatement.setString(2,TeamUUID.toString());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        return TeamUUID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JDBC_Team) {
            return TeamUUID.equals(((JDBC_Team)obj).TeamUUID);
        }
        return false;

    }
}
