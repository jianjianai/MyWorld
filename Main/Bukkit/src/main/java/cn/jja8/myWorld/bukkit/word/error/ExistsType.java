package cn.jja8.myWorld.bukkit.word.error;

import cn.jja8.myWorld.bukkit.work.error.MyWorldException;

/**
 * 世界类型已存在异常
 * */
public class ExistsType extends MyWorldException {
    public ExistsType(String s) {
        super(s);
    }
}
