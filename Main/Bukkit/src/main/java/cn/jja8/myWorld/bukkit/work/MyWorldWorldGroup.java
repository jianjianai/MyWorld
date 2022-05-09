package cn.jja8.myWorld.bukkit.work;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
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

    Worlds worlds;
    public MyWorldWorldGroup(Worlds worlds) {
        this.worlds = worlds;
    }

    /**
     * 加载这个世界组
     * @param onLoad 加载完成时返回世界组
     * */
    public void load(OnLoad onLoad){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            try {
                onLoad.onload(new MyWorldWorldGrouping(MyWorldBukkit.getPlayerWordMangaer().loadPlayerWorlds(worlds)));
            } catch (NoAllWorldLocks e) {
                onLoad.fail(e);
            }
        });
    }
}
