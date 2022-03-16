package cn.jja8.myWorld.bungeecord.basic.worldDataSupport;

public interface WorldDataLock {
    /**
     * 返回世界是否被锁
     * @return true锁了 false没锁
     * */
    boolean isLocked();
    /**
     * 获取上锁服务器的名称
     * @return null 没有被锁
     * */
    String gitLockName();

}
