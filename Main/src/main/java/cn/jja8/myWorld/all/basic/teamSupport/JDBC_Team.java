package cn.jja8.myWorld.all.basic.teamSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JDBC_Team implements Team{
    JDBC_TeamManger teamManger;
    UUID TeamUUID;


    private JDBC_Team(JDBC_TeamManger teamManger, UUID teamUUID) {
        this.teamManger = teamManger;
        TeamUUID = teamUUID;
    }

    static JDBC_Team get(JDBC_TeamManger teamManger, UUID teamUUID){
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select UUID from Team where UUID=?")){
                preparedStatement.setString(1,teamUUID.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return new JDBC_Team(teamManger,teamUUID);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
    @Override
    public String getTeamName() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select TeamName from Team where UUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return resultSet.getString(1);
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
                preparedStatement.executeQuery();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("delete from TeamPlayer where TeamUUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
                preparedStatement.executeQuery();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
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
                preparedStatement.executeQuery();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public String getWorldName() {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select WorldName from Team where UUID=?")){
                preparedStatement.setString(1,TeamUUID.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return resultSet.getString(1);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public void setWorldName(String teamName) {
        try (Connection connection = teamManger.getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("update Team set WorldName=? where UUID=?")){
                preparedStatement.setString(1,teamName);
                preparedStatement.setString(2,TeamUUID.toString());
                preparedStatement.executeQuery();
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
