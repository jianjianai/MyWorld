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
     * 解锁，并且释放资源
     * */
    void unlock();
}
