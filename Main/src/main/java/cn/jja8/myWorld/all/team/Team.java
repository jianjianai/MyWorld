package cn.jja8.myWorld.all.team;

import cn.jja8.myWorld.all.veryUtil.SQLite;

import java.util.ArrayList;
import java.util.List;

/**
 * 先写几个方法，到时候再实现。
 */
public class Team {
    int 团队ID;
    String 数据库url;
    TeamManager teamManager;
    Team(TeamManager teamManager,int 团队ID,String 数据库url){
        this.teamManager = teamManager;
        this.数据库url = 数据库url;
        this.团队ID = 团队ID;
    }
    /**
     * 获取这个团队的世界名称。
     * @return null 团队已经被删除
     */
    public String getWorldName() {
        String r = null;
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select WorldName from Team where ID=?",数据库url);
        a.setInt(1,团队ID);
        SQLite.ResultSetAndUp b = a.executeQuery();
        if (b.next()){
            r = b.getString(1);
        }
        b.close();
        a.close();
        return r;
    }

    /**
     * 设置这个团队的世界名称。
     */
    public boolean setWorldName(String name) {
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("UPDATE Team SET WorldName=? WHERE ID=?;",数据库url);
        a.setString(1,name);
        a.setInt(2,团队ID);
        boolean r = a.executeUpdate()>=1;
        a.close();
        return r;
    }

    /**
     * 获取这个团队的团长
     * @return null 团队已经被删除
     */
    public String getLeader() {
        String r = null;
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select Leader from Team where ID=?",数据库url);
        a.setInt(1,团队ID);
        SQLite.ResultSetAndUp b = a.executeQuery();
        if (b.next()){
            r = b.getString(1);
        }
        b.close();
        a.close();
        return r;
    }

    /**
     * 获取这个团队的管理员列表
     */
    public List<String> getAdminList() {
        List<String> r = new ArrayList<>();
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select PlayerName from TeamAdmin where TeamID=?",数据库url);
        a.setInt(1,团队ID);
        SQLite.ResultSetAndUp b = a.executeQuery();
        while (b.next()){
            r.add(b.getString(1));
        }
        b.close();
        a.close();
        return r;
    }

    /**
     * 获取这个团队的成员列表
     */
    public List<String> getMemberList() {
        List<String> r = new ArrayList<>();
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select PlayerName from TeamMember where TeamID=?",数据库url);
        a.setInt(1,团队ID);
        SQLite.ResultSetAndUp b = a.executeQuery();
        while (b.next()){
            r.add(b.getString(1));
        }
        b.close();
        a.close();
        return r;
    }

    /**
     * 判断这个人是不是团队成员（团长，管理员，成员都是）
     */
    public boolean isMember(String name) {
        if (isAdmin(name)){
            return true;
        }
        int r = -1;
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select TeamID from TeamMember where PlayerName=?",数据库url);
        a.setString(1,name);
        SQLite.ResultSetAndUp b = a.executeQuery();
        if (b.next()){
            r = b.getInt(1);
        }
        b.close();
        a.close();
        return r==团队ID;
    }

    /**
     * 判断这个人是不是团队管理（团长，管理员都是）
     */
    public boolean isAdmin(String name) {
        if (isLeader(name)){
            return true;
        }
        int r = -1;
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select TeamID from TeamAdmin where PlayerName=?",数据库url);
        a.setString(1,name);
        SQLite.ResultSetAndUp b = a.executeQuery();
        if (b.next()){
            r = b.getInt(1);
        }
        b.close();
        a.close();
        return r==团队ID;
    }

    /**
     * 判断这个人是不是团长
     */
    public boolean isLeader(String name) {
        if (name==null){
            return false;
        }
        return name.equals(getLeader());
    }

    /**
     * 添加成员
     */
    public boolean addMember(String name) {
        if (isMember(name)){
            return true;
        }
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("INSERT INTO TeamMember(TeamID,PlayerName) VALUES(?,?);",数据库url);
        a.setInt(1,团队ID);
        a.setString(2,name);
        boolean r = a.executeUpdate()>=1;
        a.close();
        return r;
    }

    /**
     * 添加管理
     */
    public boolean addAdmin(String name) {
        if (isAdmin(name)){
            return true;
        }
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("INSERT INTO TeamAdmin(TeamID,PlayerName) VALUES(?,?);",数据库url);
        a.setInt(1,团队ID);
        a.setString(2,name);
        boolean r = a.executeUpdate()>=1;
        a.close();
        return r;
    }

    /**
     * 删除成员
     */
    public boolean delMember(String name) {
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("DELETE FROM TeamMember WHERE PlayerName=?;",数据库url);
        a.setString(1,name);
        boolean r = a.executeUpdate()>=1;
        a.close();
        return r;
    }

    /**
     * 删除管理
     */
    public boolean delAdmin(String name) {
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("DELETE FROM TeamAdmin WHERE PlayerName=?;",数据库url);
        a.setString(1,name);
        boolean r = a.executeUpdate()>=1;
        a.close();
        return r;
    }

    /**
     * 获取团队名称
     * @return null 团队已经被删除
     */
    public String getTeamName() {
        String r = null;
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("select TeamName from Team where ID=?",数据库url);
        a.setInt(1,团队ID);
        SQLite.ResultSetAndUp b = a.executeQuery();
        if (b.next()){
            r = b.getString(1);
        }
        b.close();
        a.close();
        return r;
    }

    /**
     * 设置团队名称
     */
    public boolean setTeamName(String name) {
        if (teamManager.getTeamFromName(name)!=null){
            throw new Error("修改的名称已经被其他团队占用！");
        }
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("UPDATE Team SET TeamName=? WHERE ID=?;",数据库url);
        a.setString(1,name);
        a.setInt(2,团队ID);
        boolean r = a.executeUpdate() >= 1;
        a.close();
        return r;
    }

    public int getTeamID() {
        return 团队ID;
    }

    public boolean setLeader(String 团长) {
        SQLite.PreparedStatementAndUp a = SQLite.get预编译语句("UPDATE Team SET Leader=? WHERE ID=?;",数据库url);
        a.setString(1,团长);
        a.setInt(2,团队ID);
        boolean r = a.executeUpdate() >= 1;
        a.close();
        return r;
    }
}
