package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import org.bukkit.Bukkit;
import org.bukkit.World;

public interface RunOnCompletion {
    /**
     * 加载完成后执行
     * */
    void CompletionRun(World world);
    /**
     * 返回世界加载进度
     * @param loading -1 不支持加载进度。
     * */
    default void LoadingProgress(int loading){
        Bukkit.getLogger().info("Converting... "+loading+"%");
    };
}
