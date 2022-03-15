package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

import org.bukkit.entity.Player;

public interface PlayerDataLock {
    /**
     * 保存某玩家数据到公共的数据库中
     * */
    void saveData(Player player) ;
    /**
     * 从公共的数据库中加载某玩家的数据
     * */
    void loadData(Player player);
    /**
     * 解锁，解锁服务器名称必须等于上锁服务器，否则不能解锁。
     * @return true解锁成功 false没有解锁
     * */
    boolean unlock(String serverName);
}
