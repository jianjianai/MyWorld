package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

/**
 * 用于从同步的数据库中管理世界。
 * */
public interface WorldDataSupport {
    /**
     * 获取某世界的锁
     * */
    WorldDataLock getWorldDataLock(String WorldName,String serverName);
    /**
     * 获取上锁服务器的名称
     * @return null 没有被锁
     * */
    String gitLockServerName(String WorldName);

    /**
     * 返回这个世界是否存在
     * */
    boolean isWorldExistence(String name);
}
