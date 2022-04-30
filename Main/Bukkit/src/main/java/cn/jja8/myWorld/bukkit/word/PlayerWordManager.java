package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.myWorld.bukkit.word.error.ExistsType;
import cn.jja8.myWorld.bukkit.word.error.ExistsWorld;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.word.error.NoWorldLocks;
import cn.jja8.myWorld.bukkit.word.name.PlayerWorldTypeAtName;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

/**
 * 用于管理每个世界
 */
public class PlayerWordManager implements Listener {

    WorldConfig worldConfig =  ConfigBukkit.getWorldConfig();

    Map<World, PlayerWorlds> wordMap = new HashMap<>();
    Map<Worlds, PlayerWorlds> worldsMap = new HashMap<>();

    public PlayerWordManager() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this,MyWorldBukkit.getMyWorldBukkit());
    }


    /**
     * 加载世界组,最好在异步调用，此方法有可能阻塞线程。
     * @return null 有世界被其他服务器上锁，无法加载。
     */
    public PlayerWorlds loadPlayerWorlds(Worlds worlds) throws NoAllWorldLocks {
        synchronized (this){
            String worldsName = worlds.getWorldsName();
            PlayerWorlds playerWorlds = new PlayerWorlds(this,worlds);
            //判断主世界，地狱，末地是否存在，如果不存在就根据配置文件添加
            if (worldConfig.主世界生成器.启用){
                if (playerWorlds.getWorld(PlayerWorldTypeAtName.world)==null){
                    String worldname = worldsName+"_"+PlayerWorldTypeAtName.world;
                    try {
                        World world = playerWorlds.putWorld(PlayerWorldTypeAtName.world,worldConfig.主世界生成器,worldname);
                        Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> WorldConfig.setGameRule(worldConfig.主世界规则,world));
                    } catch (ExistsType | ExistsWorld e) {
                        e.printStackTrace();
                    } catch (NoWorldLocks e) {
                        MyWorldBukkit.getMyWorldBukkit().getLogger().warning("新的世界"+worldname+"无法获得锁。可能是已经被其他服务器加载，或世界命名规则错乱导致。"+worldsName+"世界组将会没有主世界。");
                    }
                }
            }
            if (worldConfig.地狱界生成器.启用){
                if (playerWorlds.getWorld(PlayerWorldTypeAtName.infernal)==null){
                    String worldname = worldsName+"_"+PlayerWorldTypeAtName.infernal;
                    try {
                        World world = playerWorlds.putWorld(PlayerWorldTypeAtName.infernal,worldConfig.地狱界生成器,worldname);
                        Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> WorldConfig.setGameRule(worldConfig.地狱世界规则,world));
                    } catch (ExistsType | ExistsWorld e) {
                        e.printStackTrace();
                    } catch (NoWorldLocks e) {
                        MyWorldBukkit.getMyWorldBukkit().getLogger().warning("新的世界"+worldname+"无法获得锁。可能是已经被其他服务器加载，或世界命名规则错乱导致。"+worldsName+"世界组将会没有地狱世界。");
                    }
                }
            }
            if (worldConfig.末地界生成器.启用){
                if (playerWorlds.getWorld(PlayerWorldTypeAtName.end)==null){
                    String worldname = worldsName+"_"+PlayerWorldTypeAtName.end;
                    try {
                        World world = playerWorlds.putWorld(PlayerWorldTypeAtName.end,worldConfig.末地界生成器,worldname);
                        Bukkit.getScheduler().runTask(MyWorldBukkit.getMyWorldBukkit(), () -> WorldConfig.setGameRule(worldConfig.末地界规则,world));
                    } catch (ExistsType | ExistsWorld e) {
                        e.printStackTrace();
                    } catch (NoWorldLocks e) {
                        MyWorldBukkit.getMyWorldBukkit().getLogger().warning("新的世界"+worldname+"无法获得锁。可能是已经被其他服务器加载，或世界命名规则错乱导致。"+worldsName+"世界组将会没有末地世界。");
                    }
                }
            }
            return playerWorlds;
        }
    }

    /**
     * 从已加载的世界中获取世界
     * @return 如果不是玩家世界返回null
     */
     public PlayerWorlds getBeLoadPlayerWorlds(World world){
         return wordMap.get(world);
     }

    /**
     * 从已加载的世界中获取世界
     * @return 如果世界没加载返回null
     */
    public PlayerWorlds getBeLoadPlayerWorlds(Worlds worlds){
        return worldsMap.get(worlds);
    }

    /**
     * 被加载的世界称集合
     * */
    public Set<Worlds> getWorldNames(){
        return worldsMap.keySet();
    }
    /**
     * 获取被加载的世界集合
     * */
    public Collection<PlayerWorlds> getWorlds(){
        return worldsMap.values();
    }

    /**
     * 判断这个世界是否存在
     */
    public boolean isWorldExistence(String name){
        return WorldData.worldDataSupport.isWorldExistence(name);
    }
    /**
     * 关闭世界管理器
     */
    public void close() {
        ArrayList<PlayerWorlds> arrayList = new ArrayList(worldsMap.values());
        for (PlayerWorlds playerWorlds : arrayList) {
            playerWorlds.unLoad(true);
        }
        wordMap = new HashMap<>();
        worldsMap = new HashMap<>();
    }

    /**
     * 删除世界
     */
    public void delPlayerWorlds(Worlds worlds) throws NoWorldLocks {
        PlayerWorlds po = worldsMap.get(worlds);
        if(po!=null){
            po.unLoad(false);
        }
        List<String> worldNames = worlds.getWorldList();
        List<String> worldNames1 = new ArrayList<>(worldNames);//用于删除掉没有被创建的世界
        for (String worldName : worldNames) {
            if (!WorldData.worldDataSupport.isWorldExistence(worldName)) {
                worldNames1.remove(worldName);
            }
        }
        List<WorldDataLock> worldDataLocks = new ArrayList<>();//获取所有存在世界的锁
        for (String s : worldNames1) {
            WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(new WorldCreator(s),worldConfig.服务器名称);
            if (worldDataLock!=null) {
                worldDataLocks.add(worldDataLock);
            }
        }
        //无法获取全部的锁就抛出异常
        if (worldDataLocks.size()!=worldNames1.size()){
            for (WorldDataLock worldDataLock : worldDataLocks) {
                worldDataLock.unlock();
            }
            throw new NoWorldLocks("无法获取需要删除全部世界的锁");
        }
        //删除全部世界
        for (WorldDataLock worldDataLock : worldDataLocks) {
            worldDataLock.delWorld();
            worldDataLock.unlock();
        }
        //删除世界库中的世界
        worlds.delete();
    }

    @EventHandler
    public void 玩家传送(PlayerTeleportEvent event){
        PlayerWorlds form = getBeLoadPlayerWorlds(event.getFrom().getWorld());
        PlayerWorlds to = getBeLoadPlayerWorlds(Objects.requireNonNull(event.getTo()).getWorld());
        if (form==null){
            return;
        }
        if (form==to){
            return;
        }
        form.setPlayerLeaveLocation(event.getPlayer(),event.getFrom());
    }
    @EventHandler
    public void 玩家离开服务器(PlayerQuitEvent event){
        PlayerWorlds playerWorlds = getBeLoadPlayerWorlds(event.getPlayer().getWorld());
        if (playerWorlds!=null){
            playerWorlds.setPlayerLeaveLocation(event.getPlayer(),event.getPlayer().getLocation());
        }
    }
}
