package cn.jja8.myWorld.bukkit.basic;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.teamSupport.TeamManager;
import cn.jja8.myWorld.bukkit.basic.teamSupport.TeamManager_userName;
import cn.jja8.myWorld.bukkit.basic.teamSupport.TeamManager_userUUID;

public class Teams {
    public static TeamManager teamManager = null;
    public static void load() {
        if (teamManager==null){
            if (MyWorldBukkit.getPlayerDataConfig().使用玩家名称保存数据){
                teamManager = new TeamManager_userName();
            }else {
                teamManager = new TeamManager_userUUID();
            }
        }
    }
    public static void unLoad(){
        teamManager.close();
    }
}
