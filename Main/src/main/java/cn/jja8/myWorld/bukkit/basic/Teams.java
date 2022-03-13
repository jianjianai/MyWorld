package cn.jja8.myWorld.bukkit.basic;


import cn.jja8.myWorld.all.basic.DatasheetSupport.old.JDBC_DatasheetManger;
import cn.jja8.myWorld.all.basic.DatasheetSupport.DatasheetManager;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;

import java.sql.SQLException;

public class Teams {
    public static DatasheetManager datasheetManager = null;
    public static void load(){
        if (datasheetManager ==null){
            try {
                datasheetManager = new JDBC_DatasheetManger(ConfigBukkit.getFileConfig().团队数据库URL,null,null);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
                for (int i = 0; i < 10; i++) {
                    MyWorldBukkit.getMyWorldBukkit().getLogger().severe("团队数据库连接失败！");
                }
                throw new Error("数据库连接失败");
            }
        }
    }
    public static void unLoad(){
        datasheetManager.close();
    }
}
