package cn.jja8.myWorld.all.basic.DatasheetSupport;

import java.sql.*;
import java.util.UUID;

public class JDBC_DatasheetManger implements DatasheetManager {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("sqlite驱动程序加载失败！！");
            e.printStackTrace();
        }
    }
    final String userName;
    final String PassWord;
    final String dataBaseURL;
    public JDBC_DatasheetManger(String dataBaseURL, String userName, String PassWord) throws SQLException {
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
            statement.execute("create table if not exists Team(UUID varchar(36) not null constraint Team_pk primary key,TeamName varchar not null,WorldsUUID varchar(36));");
            statement.execute("create unique index if not exists Team_TeamName_uindex on Team (TeamName);");
            statement.execute("create unique index if not exists Team_WorldsUUID_uindex on Team (WorldsUUID);");
            //teamPlayer
            statement.execute("create table if not exists TeamPlayer(PlayerUUID varchar(36) not null constraint TeamPlayer_pk primary key,PlayerName varchar,TeamUUID varchar(36),Status varchar);");
            statement.execute("create index if not exists TeamPlayer_TeamUUID_uindex on TeamPlayer (TeamUUID);");
            //Worlds
            statement.execute("create table if not exists Worlds(WorldsUUID varchar(36) not null,WorldsName varchar not null);");
            statement.execute("create unique index if not exists Worlds_WorldsName_index on Worlds(WorldsName);");
            //World
            statement.execute("create table if not exists World(WorldsUUID varchar(36) not null,WorldName varchar not null);");
            statement.execute("create index if not exists World_WorldName_index on World (WorldName);");
            statement.execute("create unique index if not exists World_WorldName_index on World (WorldName);");
            //WorldSdata
            statement.execute("create table if not exists WorldsData(WorldsUUID varchar(36) not null,DataName varchar not null,Data blob);");
            statement.execute("create unique index if not exists WorldsUUID_WorldTrust_uindex on WorldSdata(WorldsUUID,DataName);");
        }
    }
    Connection getConnection() throws SQLException {
       return DriverManager.getConnection(dataBaseURL, userName, PassWord);
    }

    @Override
    public Team getTeamFromTeamName(String teamName) {
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select UUID from Team where TeamName=?")){
                preparedStatement.setString(1,teamName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    try {
                        return new JDBC_Team(this,UUID.fromString(resultSet.getString(1)));
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
    public Worlds getWorldsFromWorldsName(String worldsName) {
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("select WorldsUUID from Worlds where WorldsName=?")){
                preparedStatement.setString(1,worldsName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    try {
                        return new JDBC_Worlds(this,UUID.fromString(resultSet.getString(1)));
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
            try (PreparedStatement preparedStatement = connection.prepareStatement("select PlayerUUID from TeamPlayer where PlayerUUID=?")){
                preparedStatement.setString(1,uuid.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return new JDBC_TeamPlayer(this,UUID.fromString(resultSet.getString(1)));
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public Team newTeam(String teamName) {
        UUID uuid = UUID.randomUUID();
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Team(UUID,TeamName) values(?,?)")){
                preparedStatement.setString(1,uuid.toString());
                preparedStatement.setString(2,teamName);
                int up = preparedStatement.executeUpdate();
                if (up==1){
                    return new JDBC_Team(this,uuid);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            if (sqlException.getErrorCode()==19){
                if (sqlException.getMessage().contains("UUID")){
                    return newTeam(teamName);
                }
            }
        }
        return null;
    }

    @Override
    public TeamPlayer newTamePlayer(UUID uuid, String name) {
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into TeamPlayer(PlayerUUID,PlayerName,Status) values(?,?,"+Status.player+")")){
                preparedStatement.setString(1,uuid.toString());
                preparedStatement.setString(2,name);
                int up = preparedStatement.executeUpdate();
                if (up==1){
                    return new JDBC_TeamPlayer(this,uuid);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    @Override
    public Worlds newWorlds(String worldName) {
        UUID uuid = UUID.randomUUID();
        try (Connection connection = getConnection()){
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into Worlds(WorldsUUID,WorldsName) values(?,?)")){
                preparedStatement.setString(1,uuid.toString());
                preparedStatement.setString(2,worldName);
                int up = preparedStatement.executeUpdate();
                if (up==1){
                    return new JDBC_Worlds(this,uuid);
                }
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            if (sqlException.getErrorCode()==19){
                if (sqlException.getMessage().contains("WorldsUUID")){
                    return newWorlds(worldName);
                }
            }
        }
        return null;
    }
}
