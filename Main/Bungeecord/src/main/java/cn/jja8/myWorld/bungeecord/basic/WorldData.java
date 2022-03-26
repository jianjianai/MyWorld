package cn.jja8.myWorld.bungeecord.basic;


import cn.jja8.myWorld.bungeecord.MyWorldBungeecord;
import cn.jja8.myWorld.bungeecord.basic.worldDataSupport.BcWorldDataAndLockSupport;
import cn.jja8.myWorld.bungeecord.basic.worldDataSupport.WorldDataSupport;

import java.io.File;

public class WorldData {
    public static WorldDataSupport worldDataSupport = null;
    public static void load() {
        if (worldDataSupport==null){
            worldDataSupport = new BcWorldDataAndLockSupport(new File(MyWorldBungeecord.getFileConfig().玩家世界文件路径));
        }
    }
}
