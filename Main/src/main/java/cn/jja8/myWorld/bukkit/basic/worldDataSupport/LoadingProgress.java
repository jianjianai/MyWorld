package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

public interface LoadingProgress {
    /**
     * 返回世界加载进度
     * 每50毫秒返回一次，-1不支持返回进度。
     * */
    default void loadingProgress(int loading){ }
}
