package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import org.bukkit.WorldCreator;

import java.util.Objects;

public class MyWorldWorld {
    final String name;
    MyWorldWorld(String name) {
        this.name = name;
    }

    /**
     * 获取世界的锁
     * */
    public MyWorldWorldLock getMyWorldWorldLock(){
        synchronized (MyWorldWorld.class){
            MyWorldWorldLock myWorldWorldLock = MyWorldManger.worldName_MyWorldWorldLock.get(name);
            if (myWorldWorldLock!=null){
                return myWorldWorldLock;
            }
            WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(new WorldCreator(name),ConfigBukkit.getWorldConfig().服务器名称);
            return worldDataLock==null?null:new MyWorldWorldLock(this,worldDataLock);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyWorldWorld that = (MyWorldWorld) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
