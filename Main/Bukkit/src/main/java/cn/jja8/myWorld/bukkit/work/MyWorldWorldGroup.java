package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.error.NoWorldLocks;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

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
    static Map<String,MyWorldWorldGrouping> groupName_myWorldWorldGroupingMap = new HashMap<>();

    WorldGroup worldGroup;
    String name;
    MyWorldWorldGroup(WorldGroup worldGroup) {
        this.worldGroup = worldGroup;
        name = worldGroup.getWorldGroupName();
    }

    /**
     * 删除这个世界组
     * */
    public void delete() throws NoWorldLocks {
        MyWorldBukkit.getPlayerWordMangaer().delPlayerWorlds(worldGroup);
    }

    /**
     * 加载这个世界组
     * @param onLoad 加载完成时返回世界组
     * */
    public void load(OnLoad onLoad){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            synchronized (MyWorldWorldGroup.class){
                MyWorldWorldGrouping myWorldWorldGrouping = groupName_myWorldWorldGroupingMap.get(name);
                if (myWorldWorldGrouping!=null){
                    onLoad.onload(myWorldWorldGrouping);
                }
                try {
                    myWorldWorldGrouping = new MyWorldWorldGrouping(MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worldGroup),this);
                    groupName_myWorldWorldGroupingMap.put(name,myWorldWorldGrouping);
                    onLoad.onload(myWorldWorldGrouping);
                } catch (NoAllWorldLocks e) {
                    onLoad.fail(e);
                }
            }
        });
    }
}
