package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.work.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.error.NoWorldLocks;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.List;


/**
 * 代表一个世界组
 * */
public class MyWorldWorldGroup {


    public static interface OnLoad{
        /**
         * 加载完成时
         * */
        void onload(MyWorldWorldGrouping worldGrouping);
        /**
         * 加载失败
         * @param exception 可能抛出 NoAllWorldLocks 或其他
         */
        void fail(Exception exception);
    }

    WorldGroup worldGroup;
    String name;
    MyWorldWorldGroup(WorldGroup worldGroup) {
        this.worldGroup = worldGroup;
        name = worldGroup.getWorldGroupName();
    }

    /**
     * 获取世界组名称
     * */
    public String getName() {
        return name;
    }

    /**
     * 添加世界到组中
     * */
    public MyWorldWorldGroupWorld putWorld(MyWorldWorld world){
        worldGroup.addWorld(world.name);
        return new MyWorldWorldGroupWorld(this,world);
    }

    /**
     * 获取组中的世界
     * */
    public MyWorldWorldGroupWorld getWorld(String worldName){
        if (!worldGroup.containsWorld(worldName)) {
            return null;
        }
        return new MyWorldWorldGroupWorld(this,new MyWorldWorld(worldName));
    }

    /**
     * 获取组中的所有世界
     * */
    public List<MyWorldWorldGroupWorld> getWorldList(){
        List<MyWorldWorldGroupWorld> worlds = new ArrayList<>();
        for (String s : worldGroup.getWorldList()) {
            worlds.add(new MyWorldWorldGroupWorld(this,new MyWorldWorld(s)));
        }
        return worlds;
    }



    /**
     * 删除这个世界组
     * */
    public void delete() throws NoWorldLocks {
        MyWorldWorldGrouping po = MyWorldManger.groupName_myWorldWorldGrouping.get(name);
        if(po!=null){
            po.unLoad(false);
        }
        List<String> worldNames = worldGroup.getWorldList();
        List<String> worldNames1 = new ArrayList<>(worldNames);//用于删除掉没有被创建的世界
        for (String worldName : worldNames) {
            if (!WorldData.worldDataSupport.isWorldExistence(worldName)) {
                worldNames1.remove(worldName);
            }
        }
        List<WorldDataLock> worldDataLocks = new ArrayList<>();//获取所有存在世界的锁
        for (String s : worldNames1) {
            WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(new WorldCreator(s), ConfigBukkit.getWorldConfig().服务器名称);
            if (worldDataLock!=null) {
                worldDataLocks.add(worldDataLock);
            }
        }
        //无法获取全部的锁就抛出异常
        if (worldDataLocks.size()!=worldNames1.size()){
            for (WorldDataLock worldDataLock : worldDataLocks) {
                worldDataLock.unlock();
            }
            throw new NoWorldLocks("无法获取需要删除全部世界的锁");
        }
        //删除全部世界
        for (WorldDataLock worldDataLock : worldDataLocks) {
            worldDataLock.delWorld();
            worldDataLock.unlock();
        }
        //删除世界库中的世界
        worldGroup.delete();
    }

    /**
     * 获取已经加载的本世界组
     * @return null 没有被加载
     * */
    public MyWorldWorldGrouping getLoaded(){
        return MyWorldManger.groupName_myWorldWorldGrouping.get(name);
    }

    /**
     * 加载这个世界组
     * @param onLoad 加载完成时返回世界组,此时是在异步运行
     * */
    public void load(OnLoad onLoad){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            synchronized (MyWorldWorldGroup.class){
                MyWorldWorldGrouping myWorldWorldGrouping = getLoaded();
                if (myWorldWorldGrouping!=null){
                    onLoad.onload(myWorldWorldGrouping);
                }
                try {
                    myWorldWorldGrouping = new MyWorldWorldGrouping(this);
                    MyWorldManger.groupName_myWorldWorldGrouping.put(name,myWorldWorldGrouping);
                    onLoad.onload(myWorldWorldGrouping);
                } catch (NoAllWorldLocks e) {
                    onLoad.fail(e);
                }
            }
        });
    }




}
