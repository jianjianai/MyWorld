package cn.jja8.myWorld.all.data.team;


import cn.jja8.myWorld.all.veryUtil.SQLite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 先写几个方法，到时候再实现。
 */
public class TeamManager {
    String 数据库url;
    public TeamManager(String 数据库url){
        this.数据库url = 数据库url;
        //初始化数据库
        String sql = "create table IF NOT EXISTS Team(ID INTEGER primary key,TeamName text not null,Leader text,WorldName text);" ;
        String sql1 = "create table IF NOT EXISTS TeamAdmin(ID INTEGER primary key,TeamID int not null,PlayerName text);" ;
        String sql2 = "create table IF NOT EXISTS TeamMember(ID INTEGER primary key,TeamID int not null,PlayerName text);" ;
        String sql3 = "create index IF NOT EXISTS TeamAdmin1 on TeamAdmin (PlayerName);" ;
        String sql4 = "create index IF NOT EXISTS TeamAdmin2 on TeamAdmin (TeamID);" ;
        String sql5 = "create index IF NOT EXISTS TeamMember1 on TeamMember (PlayerName);" ;
        String sql6 = "create index IF NOT EXISTS TeamMember2 on TeamMember (TeamID);" ;
        String sql7 = "create unique index IF NOT EXISTS Team1 on Team (Leader);" ;
        String sql8 = "create unique index IF NOT EXISTS Team2 on Team (TeamName);";
        try {
            Connection cow = SQLite.git连接(数据库url);
            Statement con = cow.createStatement();
            con.execute(sql);
            con.execute(sql1);
            con.execute(sql2);
            con.execute(sql3);
            con.execute(sql4);
            con.execute(sql5);
            con.execute(sql6);
            con.execute(sql7);
            con.execute(sql8);
            con.close();
            cow.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    /**
     * 通过团队名称获取团队,如果没有返回null
     */
    public Team getTeamFromName(String teamName) {
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select ID from Team where TeamName=?",数据库url);
        a.setString(1,teamName);
        SQLite.ResultSetAndUp b = a.executeQuery();
        if (b.next()){
            int id = b.getInt(1);
            b.close();
            a.close();
            return new Team(this,id,数据库url);
        }
        b.close();
        a.close();
        return null;
    }

    /**
     * 通过玩家名称获取所在团队，如果没有返回null
     */
    public Team getTeamFromPlayer(String playerName) {
        {
            //查询成员
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select TeamID from TeamMember where PlayerName=?",数据库url);
            a.setString(1,playerName);
            SQLite.ResultSetAndUp b = a.executeQuery();
            if (b.next()){
                Team r = new Team(this,b.getInt(1),数据库url);
                b.close();
                a.close();
                return r;
            }
            b.close();
            a.close();
        }
        {
            //查询团队管理员
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select TeamID from TeamAdmin where PlayerName=?",数据库url);
            a.setString(1,playerName);
            SQLite.ResultSetAndUp b = a.executeQuery();
            if (b.next()){
                Team r = new Team(this,b.getInt(1),数据库url);
                b.close();
                a.close();
                return r;
            }
            b.close();
            a.close();
        }
        {
            //查询团长
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select ID from Team where Leader=?",数据库url);
            a.setString(1,playerName);
            SQLite.ResultSetAndUp b = a.executeQuery();
            if (b.next()){
                Team r = new Team(this,b.getInt(1),数据库url);
                b.close();
                a.close();
                return r;
            }
            b.close();
            a.close();
        }
        return null;
    }

    /**
     * 创建团队
     */
    public Team newTeam(String TeamName, String leader) {
        Team team = getTeamFromName(TeamName);
        if (team==null){
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("INSERT INTO Team(TeamName,Leader) VALUES(?,?);",数据库url);
            a.setString(1,TeamName);
            a.setString(2,leader);
            a.execute();
            a.close();
            return getTeamFromName(TeamName);
        }else {
            team.setLeader(leader);
            return team;
        }
    }

    /**
     * 删除团队
     */
    public boolean delTeam(String teamName) {
        Team team = getTeamFromName(teamName);
        if (team==null){
            return false;
        }
        int teamID = team.getTeamID();
        {//删除团队成员
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("DELETE FROM TeamMember WHERE TeamID=?;",数据库url);
            a.setInt(1,teamID);
            a.execute();
            a.close();
        }
        {//删除团队管理员
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("DELETE FROM TeamAdmin WHERE TeamID=?;",数据库url);
            a.setInt(1,teamID);
            a.execute();
            a.close();
        }
        {//删除团队
            SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("DELETE FROM Team WHERE ID=?;",数据库url);
            a.setInt(1,teamID);
            a.execute();
            a.close();
        }
        return true;
    }

    public void close() {
    }
}
