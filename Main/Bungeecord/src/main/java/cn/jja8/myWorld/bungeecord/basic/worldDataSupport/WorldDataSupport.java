package cn.jja8.myWorld.bungeecord.basic.worldDataSupport;

/**
 * 用于从同步的数据库中管理世界。
 * */
public interface WorldDataSupport {
    /**
     * 获取某世界的锁
     * */
    WorldDataLock getWorldDataLock(String WorldName);
    /**
     * 返回这个世界是否存在
     * */
    boolean isWorldExistence(String name);
}
