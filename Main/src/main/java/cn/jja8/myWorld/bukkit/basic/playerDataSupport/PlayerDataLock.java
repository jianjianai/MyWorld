package cn.jja8.myWorld.bukkit.basic.playerDataSupport;

public interface PlayerDataLock {
    /**
     * 返回是否被锁
     * */
    boolean isLocked();
    /**
     * 上锁，如果世界已经被锁了就不能再次上锁。
     * @return true上锁成功 false上锁失败
     * */
    boolean locked(String serverName);
    /**
     * 解锁，解锁服务器名称必须等于上锁服务器，否则不能解锁。
     * @return true解锁成功 false没有解锁
     * */
    boolean unlock(String serverName);
}
