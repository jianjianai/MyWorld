package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

public interface WorldDataLock {
    /**
     * 给世界解锁，解锁服务器名称必须等于上锁服务器，否则不能解锁。
     * @return true解锁成功 false没有解锁
     * */
    boolean unlock(String serverName);
}
