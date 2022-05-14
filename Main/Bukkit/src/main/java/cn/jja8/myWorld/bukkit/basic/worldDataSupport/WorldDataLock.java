package cn.jja8.myWorld.bukkit.basic.worldDataSupport;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.concurrent.atomic.AtomicReference;

public interface WorldDataLock {
    /**
     * 解锁，并且解锁释放资源
     * */
    void unlock();
    /**
     * 卸载当前世界
     * @param save 是否保存这个世界的数据。true是，false否。
     * */
    void unloadWorld(boolean save);
    /**
     * 获取世界生成器
     * */
    WorldCreator getWorldCreator();
    /**
     * 加载当前世界
     * */
    World loadWorld(LoadingProgress loadingProgress);
    /**
     * 异步加载世界,此方法可在异步调用,如果没有被实现，就会调用loadWorld(),并等待加载完成
     * */
    default World loadWorldAsync(LoadingProgress loadingProgress){
        Bukkit.getLogger().warning("异步加载世界方式未被实现，将在主线程加载世界");
        AtomicReference<World> world = new AtomicReference<>();
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        Bukkit.getServer().getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> {
            try {
                world.set(loadWorld(loadingProgress));
            }catch (Throwable e){
                throwable.set(e);
            }

        });
        while (true){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (world.get()!=null){
                break;
            }
            if (throwable.get() !=null){
                throw new Error(throwable.get());
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
     * 删除掉当前世界
     * */
    void delWorld();
}
