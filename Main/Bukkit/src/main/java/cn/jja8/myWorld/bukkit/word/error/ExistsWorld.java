package cn.jja8.myWorld.bukkit.word.error;

import cn.jja8.myWorld.bukkit.work.error.MyWorldException;

/**
 * 世界已经存在异常
 * */
public class ExistsWorld extends MyWorldException {
    public ExistsWorld(String s) {
        super(s);
    }
}
