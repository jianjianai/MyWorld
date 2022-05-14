package cn.jja8.myWorld.bukkit.word.error;

import cn.jja8.myWorld.bukkit.work.error.MyWorldException;

/**
 * 无法获取确保世界的锁异常
 * */
public class NoAllWorldLocks extends MyWorldException {
    public NoAllWorldLocks(String s) {
        super(s);
    }
}
