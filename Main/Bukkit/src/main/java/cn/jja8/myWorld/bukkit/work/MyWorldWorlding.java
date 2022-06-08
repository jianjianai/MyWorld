package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.basic.WorldData;
import org.bukkit.World;

/**
 * 代表一个正在服务器中运行的世界
 * */
public class MyWorldWorlding {
    MyWorldWorldLock myWorldWorldLock;
    World world;
    MyWorldWorlding(MyWorldWorldLock myWorldWorldLock) {
        this.myWorldWorldLock = myWorldWorldLock;

        LoadingProgress loadingProgress =  new LoadingProgress(myWorldWorldLock.myWorldWorld.name);
        boolean fast = !WorldData.worldDataSupport.isWorldExistence(myWorldWorldLock.myWorldWorld.name);
        world = myWorldWorldLock.worldDataLock.loadWorldAsync(loadingProgress);
        loadingProgress.finish();
        MyWorldManger.worldName_MyWorldWorlding.put(myWorldWorldLock.myWorldWorld.name,this);
        if (fast){
            myWorldWorldLock.myWorldWorldInform.getMyWorldWorldCreator().setting(world);
        }
    }

    /**
     * 卸载世界
     * @param save 是否保存数据
     * */
    public void unLoad(boolean save){
        myWorldWorldLock.worldDataLock.unloadWorld(save);
        MyWorldManger.worldName_MyWorldWorlding.remove(myWorldWorldLock.myWorldWorld.name);
        world=null;
    }

    /**
     * 获得世界
     * */
    public World getWorld() {
        return world;
    }

}
