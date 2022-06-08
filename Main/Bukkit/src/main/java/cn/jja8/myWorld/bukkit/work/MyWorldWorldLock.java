package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import org.bukkit.Bukkit;

/**
 * 一个世界的锁
 * */
public class MyWorldWorldLock {
    public static interface OnLoad{
        /**
         * 加载完成时
         * */
        void onload(MyWorldWorlding myWorldWorlding);
        /**
         * 加载失败
         */
        void fail(Exception exception);
    }

    MyWorldWorld myWorldWorld;
    WorldDataLock worldDataLock;
    MyWorldWorldInform myWorldWorldInform;
    public MyWorldWorldLock(MyWorldWorld myWorldWorld, WorldDataLock worldDataLock) {
        this.myWorldWorld = myWorldWorld;
        this.worldDataLock = worldDataLock;
        myWorldWorldInform = new MyWorldWorldInform(this);
        worldDataLock.getWorldCreator().copy(myWorldWorldInform.getMyWorldWorldCreator().getWorldCreator(myWorldWorld.name));
        MyWorldManger.worldName_MyWorldWorldLock.put(myWorldWorld.name,this);
    }

    public void delete(){
        worldDataLock.delWorld();
        worldDataLock.unlock();
        MyWorldManger.worldName_MyWorldWorldLock.remove(myWorldWorld.name);
    }

    /**
     * 获得世界信息
     * */
    public MyWorldWorldInform getMyWorldWorldInform() {
        return myWorldWorldInform;
    }

    /**
     * 解锁
     * @param save 是否保存数据
     * */
    public void unlock(boolean save){
        MyWorldWorlding myWorldWorlding = MyWorldManger.worldName_MyWorldWorlding.get(myWorldWorld.name);
        if (myWorldWorlding!=null){
            myWorldWorlding.unLoad(save);
        }
        if (save){
            myWorldWorldInform.save();
        }
        worldDataLock.unlock();
        MyWorldManger.worldName_MyWorldWorldLock.remove(myWorldWorld.name);
    }

    /**
     * 加载世界
     * */
    public void loadWorld(OnLoad onLoad){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            synchronized (MyWorldWorldLock.class){
                MyWorldWorlding myWorldWorlding = MyWorldManger.worldName_MyWorldWorlding.get(myWorldWorld.name);
                if (myWorldWorlding!=null){
                    onLoad.onload(myWorldWorlding);
                    return;
                }
                onLoad.onload(new MyWorldWorlding(MyWorldWorldLock.this));
            }
        });
    }


}
