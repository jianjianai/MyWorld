package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import org.bukkit.WorldCreator;

/**
 * 用于从同步的数据库中管理世界。
 * */
public interface WorldDataSupport {
    /**
     * 获取某世界的锁，creator名称就是世界名称。
     * @return null 世界已经被其他服务器上锁
     * */
    WorldDataLock getWorldDataLock(WorldCreator creator, String serverName);
    /**
     * 获取上锁服务器的名称
     * @return null 没有被锁
     * */
    String gitLockServerName(String worldName);

    /**
     * 返回这个世界是否存在
     * */
    boolean isWorldExistence(String worldName);
}
