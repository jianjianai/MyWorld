package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

public interface PlayerDataLock {
    /**
     * 保存玩家数据到公共的数据库中
     * */
    void saveData() ;
    /**
     * 从公共的数据库中加载玩家的数据
     * */
    void loadData();
    /**
     * 解锁，并且释放资源
     * */
    void unlock();
}
