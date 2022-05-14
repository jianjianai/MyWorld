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
        WorldGroup worldGroup = team.getWorldGroup();
        if (worldGroup ==null) {
            worldGroup = datasheetManager.newWorldGroup("asdasdasd");
            team.setWorlds(worldGroup);
        }
        System.out.println(worldGroup.getWorldList());

        WorldGroupData worldGroupData = worldGroup.getWorldGroupData("asdasddqwe");
        if (worldGroupData ==null) {
            worldGroupData = worldGroup.newWorldGroupData("asdasddqwe");
        }
        byte[] bytes = worldGroupData.getData();
        if (bytes==null) {
            bytes = "大帅哥啊啊啊啊".getBytes(StandardCharsets.UTF_8);
            worldGroupData.setData(bytes);
            System.out.println("wwwwwww");
        }
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

}
