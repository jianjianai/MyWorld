package cn.jja8.myWorld.bukkit.word.error;

/**
 * 世界类型已存在异常
 * */
public class ExistsType extends Throwable {
    public ExistsType(String s) {
        super(s);
    }
}