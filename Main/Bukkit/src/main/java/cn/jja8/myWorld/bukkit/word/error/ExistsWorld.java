package cn.jja8.myWorld.bukkit.word.error;

/**
 * 世界已经存在异常
 * */
public class ExistsWorld extends Throwable {
    public ExistsWorld(String s) {
        super(s);
    }
}
