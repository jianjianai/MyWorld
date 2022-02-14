package cn.jja8.myWorld.all.basic.teamSupport;

import java.sql.*;
import java.util.UUID;

public class JDBC_TeamManger implements TeamManager {
    final String userName;
    final String PassWord;
    final String dataBaseURL;
    public JDBC_TeamManger(String dataBaseURL,String userName,String PassWord) throws SQLException {
        this.dataBaseURL = dataBaseURL;
        this.userName = userName;
        this.PassWord = PassWord;
        try (Connection connection = DriverManager.getConnection(dataBaseURL, userName, PassWord)){
            initialization(connection);
        }
    }

    private void initialization(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()){
            //team
            statement.execute("create table if not exists Team(UUID varchar(36) not null constraint Team_pk primary key,TeamName varchar not null,WorldName varchar);");
            statement.execute("create unique index if not exists Team_TeamName_uindex on Team (TeamName);");
            statement.execute("create unique index if not exists Team_WorldName_uindex on Team (WorldName);");
            //teamPlayer
            statement.execute("create table if not exists TeamPlayer(PlayerUUID varchar(36) not null constraint TeamPlayer_pk primary key,TeamUUID varchar(36),Status varchar);");
            statement.execute("create unique index if not exists TeamPlayer_TeamUUID_uindex on TeamPlayer (TeamUUID);");
        }
    }
    Connection getConnection() throws SQLException {
       return DriverManager.getConnection(dataBaseURL, userName, PassWord);
    }

    @Override
    public Team getTeam(String teamName) {
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select UUID from Team where TeamName=?")){
                preparedStatement.setString(1,teamName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    try {
                        return JDBC_Team.get(this,UUID.fromString(resultSet.getString(1)));
                    }catch (IllegalArgumentException illegalArgumentException){
                        illegalArgumentException.printStackTrace();
                    }
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public TeamPlayer getTamePlayer(UUID uuid) {
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select TeamPlayer from TeamPlayer where PlayerUUID=?")){
                preparedStatement.setString(1,uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return new JDBC_TeamPlayer(this,uuid);
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into TeamPlayer(PlayerUUID) values(?)")){
                preparedStatement.setString(1,uuid.toString());
                int up = preparedStatement.executeUpdate();
                if (up==1){
                    return new JDBC_TeamPlayer(this,uuid);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        throw new Error("数据库连接出现异常！");
    }

    @Override
    public Team newTeam(String teamName) {
        UUID uuid = UUID.randomUUID();
        try (Connection connection = getConnection()){
            while (true){
                try (PreparedStatement preparedStatement = connection.prepareStatement("select UUID from Team where UUID=?")){
                    preparedStatement.setString(1,uuid.toString());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (!resultSet.next()){
                        break;
                    }
                    uuid = UUID.randomUUID();
                }
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Team(UUID,TeamName) values(?,?)")){
                preparedStatement.setString(1,uuid.toString());
                preparedStatement.setString(2,teamName);
                int up = preparedStatement.executeUpdate();
                if (up==1){
                    return JDBC_Team.get(this,uuid);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }
}
