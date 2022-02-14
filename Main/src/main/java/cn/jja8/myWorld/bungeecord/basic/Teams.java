package cn.jja8.myWorld.bungeecord.basic;

import cn.jja8.myWorld.all.basic.teamSupport.JDBC_TeamManger;
import cn.jja8.myWorld.all.basic.teamSupport.TeamManager;
import cn.jja8.myWorld.bungeecord.MyWorldBungeecord;

import java.sql.SQLException;

public class Teams {
    public static TeamManager teamManager = null;
    public static void load(){
        if (teamManager==null){
            try {
                teamManager = new JDBC_TeamManger(MyWorldBungeecord.getFileConfig().团队数据库URL,null,null);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                for (int i = 0; i < 10; i++) {
                    MyWorldBungeecord.getMyWorldBungeecord().getLogger().severe("团队数据库连接失败！");
                }
                throw new Error("数据库连接失败");
            }
        }
    }
    public static void unLoad(){
        teamManager.close();
    }
}
