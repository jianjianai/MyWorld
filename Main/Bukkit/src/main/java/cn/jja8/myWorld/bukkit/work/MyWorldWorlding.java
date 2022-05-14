package cn.jja8.myWorld.bukkit.work;

import org.bukkit.World;

public class MyWorldWorlding {
    MyWorldWorldLock myWorldWorldLock;
    World world;
    MyWorldWorlding(MyWorldWorldLock myWorldWorldLock) {
        this.myWorldWorldLock = myWorldWorldLock;

        MyWorldWorldLock.LoadingProgress loadingProgress =  new MyWorldWorldLock.LoadingProgress(myWorldWorldLock.myWorldWorld.name);
        world = myWorldWorldLock.worldDataLock.loadWorldAsync(loadingProgress);
        loadingProgress.finish();

        MyWorldManger.worldName_MyWorldWorlding.put(myWorldWorldLock.myWorldWorld.name,this);
    }

    /**
     * 卸载世界
     * @param save 是否保存数据
     * */
    public void unLoad(boolean save){
        myWorldWorldLock.worldDataLock.unloadWorld(save);
    }

    /**
     * 获得世界
     * */
    public World getWorld() {
        return world;
    }

}
