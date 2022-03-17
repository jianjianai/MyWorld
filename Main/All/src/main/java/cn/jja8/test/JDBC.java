package cn.jja8.test;

import cn.jja8.myWorld.all.basic.DatasheetSupport.*;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class JDBC {
    public static void main(String[] args) throws SQLException {
        DatasheetManager datasheetManager = new JDBC_DatasheetManger("jdbc:sqlite:identifier.sqlite",null,null);
        Team team = datasheetManager.getTeamFromTeamName("zdxhsdjtfsd");
        if (team==null) {
            team = datasheetManager.newTeam("zdxhsdjtfsd");
        }
        Worlds worlds = team.getWorlds();
        if (worlds==null) {
            worlds = datasheetManager.newWorlds("asdasdasd");
            team.setWorlds(worlds);
        }
        System.out.println(worlds.getWorldList());

        WorldsData worldsData = worlds.getWorldsData("asdasddqwe");
        if (worldsData==null) {
            worldsData = worlds.newWorldsData("asdasddqwe");
        }
        byte[] bytes = worldsData.getData();
        if (bytes==null) {
            bytes = "大帅哥啊啊啊啊".getBytes(StandardCharsets.UTF_8);
            worldsData.setData(bytes);
            System.out.println("wwwwwww");
        }
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

}
