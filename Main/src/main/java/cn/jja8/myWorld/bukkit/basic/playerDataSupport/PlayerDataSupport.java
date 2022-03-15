package cn.jja8.myWorld.bukkit.basic.playerDataSupport;
import org.bukkit.entity.Player;

/**
 * 用于在同步的数据库中管理玩家数据
 * */
public interface PlayerDataSupport {
    /**
     * 保存某玩家数据到公共的数据库中
     * */
    void saveData(Player player) ;
    /**
     * 从公共的数据库中加载某玩家的数据
     * */
    void loadData(Player player);
    /**
     * 获得这个玩家的锁
     * @return null 此玩家已经被锁
     * @param serverName 上锁服务器名称
     * */
    PlayerDataLock getPlayerDataLock(Player player,String serverName);
}
