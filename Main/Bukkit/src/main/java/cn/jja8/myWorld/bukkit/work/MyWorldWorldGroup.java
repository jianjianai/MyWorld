package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.error.NoWorldLocks;
import org.bukkit.Bukkit;


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
     * 删除这个世界组
     * */
    public void delete() throws NoWorldLocks {
        MyWorldBukkit.getPlayerWordMangaer().delPlayerWorlds(worldGroup);
    }

    /**
     * 获取已经加载的本世界组
     * @return null 没有被加载
     * */
    public MyWorldWorldGrouping getLoaded(){
        return MyWorldManger.groupName_myWorldWorldGroupingMap.get(name);
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
                    myWorldWorldGrouping = new MyWorldWorldGrouping(MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worldGroup),this);
                    MyWorldManger.groupName_myWorldWorldGroupingMap.put(name,myWorldWorldGrouping);
                    onLoad.onload(myWorldWorldGrouping);
                } catch (NoAllWorldLocks e) {
                    onLoad.fail(e);
                }
            }
        });
    }


}
