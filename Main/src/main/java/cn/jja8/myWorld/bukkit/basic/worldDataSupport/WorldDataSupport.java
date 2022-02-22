package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于从同步的数据库中管理世界。
 * */
public interface WorldDataSupport {
    /**
     * 卸载掉一个世界
     * @param save 是否保存这个世界的数据。true是，false否。
     * */
    boolean unloadWorld(World world, boolean save);
    /**
     * 加载指定名称的世界
     * */
    World loadWorld(WorldCreator creator, String worldName);
    /**
     * 异步加载指定名称的世界,如果没有被实现，就会调用loadWorld()
     * */
    default void loadWorldAsync(WorldCreator creator, String worldName, RunOnCompletion run){
        Bukkit.getLogger().warning("异步加载世界方式未被实现，将在主线程加载世界："+worldName);
        Bukkit.getServer().getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
            run.LoadingProgress(-1);
            run.CompletionRun(loadWorld(creator,worldName));
            run.LoadingProgress(100);
        });
    }
    /**
     * 获取某世界的锁
     * */
    WorldDataLock getWorldDataLock(String WorldName);
    /**
     * 获取某世界的自定义数据输入流
     * @return 如果没有，返回null
     * */
    InputStream getCustomDataInputStream(String WorldName,String dataName);
    /**
     * 获取某世界的自定义数据输出流
     * @return 不可以是null
     * */
    OutputStream getCustomDataOutputStream(String WorldName,String dataName);

    /**
     * 删除掉指定世界
     * */
    void delWorld(String wordName);

    /**
     * 返回这个世界是否存在
     * */
    boolean isWorldExistence(String name);
}
