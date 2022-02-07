package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

public interface WorldDataLock {
    /**
     * 返回世界是否被锁
     * @return true锁了 false没锁
     * */
    boolean isLocked();
    /**
     * 给世界上锁，如果世界已经被锁了就不能再次上锁.
     * @return true上锁成功 false上锁失败
     * */
    boolean locked(String serverName);
    /**
     * 给世界解锁，解锁服务器名称必须等于上锁服务器，否则不能解锁。
     * @return true解锁成功 false没有解锁
     * */
    boolean unlock(String serverName);
    /**
     * 获取上锁服务器的名称
     * @return null 没有被锁
     * */
    String gitLockName();

}
