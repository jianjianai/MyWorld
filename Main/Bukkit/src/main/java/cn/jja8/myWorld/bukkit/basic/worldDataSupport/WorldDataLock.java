package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.concurrent.atomic.AtomicReference;

public interface WorldDataLock {
    /**
     * 卸载当前世界，并且解锁释放资源
     * @param save 是否保存这个世界的数据。true是，false否。
     * */
    void unloadWorldAndUnlock(boolean save);
    /**
     * 加载当前世界
     * */
    World loadWorld(WorldCreator creator,LoadingProgress loadingProgress);
    /**
     * 异步加载世界,此方法可在异步调用,如果没有被实现，就会调用loadWorld(),并等待加载完成
     * */
    default World loadWorldAsync(WorldCreator creator, LoadingProgress loadingProgress){
        Bukkit.getLogger().warning("异步加载世界方式未被实现，将在主线程加载世界："+creator.name());
        AtomicReference<World> world = new AtomicReference<>();
        Bukkit.getServer().getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> world.set(loadWorld(creator, loadingProgress)));
        while (world.get()==null){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return world.get();
    }
    /**
     * 获取某世界的自定义数据输
     * @return 如果没有，返回null
     * */
    byte[] getCustomDataByte(String dataName);
    /**
     * 获取某世界的自定义数据
     * */
    void setCustomDataByte(String dataName,byte[] bytes);

    /**
     * 删除掉当前世界，并且解锁释放资源
     * */
    void delWorldAndUnlock();
}
