package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import org.bukkit.WorldCreator;

public class MyWorldWorld {
    final String name;
    MyWorldWorld(String name) {
        this.name = name;
    }

    /**
     * 获取世界的锁
     * */
    public MyWorldWorldLock getMyWorldWorldLock(){
        MyWorldWorldLock myWorldWorldLock = MyWorldManger.worldName_MyWorldWorldLock.get(name);
        if (myWorldWorldLock!=null){
            return myWorldWorldLock;
        }
        WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(new WorldCreator(name),ConfigBukkit.getWorldConfig().服务器名称);
        return worldDataLock==null?null:new MyWorldWorldLock(this,worldDataLock);
    }

}
