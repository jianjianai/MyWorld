package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.Teams;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
    public PlayerWorlds loadPlayerWorlds(Worlds worlds){
        synchronized (this){
            PlayerWorlds po = worldsMap.get(worlds);
            if (po!=null){
                return po;
            }

            String worldsName = worlds.getWorldsName();
            PlayerWordInform playerWordInform = new PlayerWordInform(worlds.getWorldsData("playerWordInform"));
            PlayerWorlds playerWorlds = new PlayerWorlds(this,playerWordInform,worldsName,worlds);

            List<String> worldList = worlds.getWorldList();

            Map<WorldDataLock,String> worldDataLockNameMap = new HashMap<>();


            //加载所有世界的锁
            for (String s : worldList) {
                WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(s,worldConfig.服务器名称);
                if (worldDataLock!=null) {
                    worldDataLockNameMap.put(worldDataLock,s);
                }
            }
            //判断世界全部上锁
            if (worldDataLockNameMap.size()<worldList.size()){
                for (WorldDataLock worldDataLock : worldDataLockNameMap.keySet()) {
                    worldDataLock.unlock();
                }
                return null;
            }

            //获取全部世界的类型
            Map<WorldDataLock,String> worldTypeMap = new HashMap<>();//最后用于加载到PlayerWorlds的map

            for (WorldDataLock worldDataLock : worldDataLockNameMap.keySet()) {
                byte[] bytes = worldDataLock.getCustomDataByte("WorldType");
                if (bytes==null){
                    worldTypeMap.put(worldDataLock,PlayerWorldTypeAtName.unknown.toString());
                }
                worldTypeMap.put(worldDataLock,new String(bytes,StandardCharsets.UTF_8));
            }

            //判断主世界，地狱，末地是否存在，如果不存在就根据配置文件添加
            if (worldConfig.主世界生成器.启用){
                if (!worldTypeMap.containsValue(PlayerWorldTypeAtName.world.toString())){
                    String worldname = worldsName+"_"+PlayerWorldTypeAtName.world;
                    WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldname,worldConfig.服务器名称);
                    if (worldDataLock!=null){
                        worldTypeMap.put(worldDataLock,PlayerWorldTypeAtName.world.toString());
                        worldDataLockNameMap.put(worldDataLock,worldname);
                        worlds.putWorld(worldname);
                    }else {
                        MyWorldBukkit.getMyWorldBukkit().getLogger().warning("新的世界"+worldname+"无法获得锁。可能是已经被其他服务器加载，或世界命名规则错乱导致。"+worldsName+"世界组将会没有主世界。");
                    }
                }
            }
            if (worldConfig.地狱界生成器.启用){
                if (!worldTypeMap.containsValue(PlayerWorldTypeAtName.infernal.toString())){
                    String worldname = worldsName+"_"+PlayerWorldTypeAtName.infernal;
                    WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldname,worldConfig.服务器名称);
                    if (worldDataLock!=null){
                        worldTypeMap.put(worldDataLock,PlayerWorldTypeAtName.infernal.toString());
                        worldDataLockNameMap.put(worldDataLock,worldname);
                        worlds.putWorld(worldname);
                    }else {
                        MyWorldBukkit.getMyWorldBukkit().getLogger().warning("新的世界"+worldname+"无法获得锁。可能是已经被其他服务器加载，或世界命名规则错乱导致。"+worldsName+"世界组将会没有地狱世界。");
                    }
                }
            }
            if (worldConfig.末地界生成器.启用){
                if (!worldTypeMap.containsValue(PlayerWorldTypeAtName.end.toString())){
                    String worldname = worldsName+"_"+PlayerWorldTypeAtName.end;
                    WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldname,worldConfig.服务器名称);
                    if (worldDataLock!=null){
                        worldTypeMap.put(worldDataLock,PlayerWorldTypeAtName.end.toString());
                        worldDataLockNameMap.put(worldDataLock,worldname);
                        worlds.putWorld(worldname);
                    }else {
                        MyWorldBukkit.getMyWorldBukkit().getLogger().warning("新的世界"+worldname+"无法获得锁。可能是已经被其他服务器加载，或世界命名规则错乱导致。"+worldsName+"世界组将会没有末地世界。");
                    }
                }
            }

            //将全部世界加载到PlayerWorlds
            worldTypeMap.forEach((worldDataLock, s) -> {
                byte[] bytes = worldDataLock.getCustomDataByte("WorldCreator");
                WorldConfig.WorldBuilder worldBuilder = null;
                if (bytes==null){
                    PlayerWorldTypeAtName playerWorldTypeAtName;
                    try {
                        playerWorldTypeAtName = PlayerWorldTypeAtName.valueOf(s);
                    }catch (IllegalArgumentException illegalArgumentException){
                        playerWorldTypeAtName = null;
                    }
                    if (playerWorldTypeAtName==null){
                        worldBuilder = worldConfig.主世界生成器;
                    }else {
                        switch (playerWorldTypeAtName){
                            case world:
                            case unknown:worldBuilder = worldConfig.主世界生成器;break;
                            case infernal:worldBuilder = worldConfig.地狱界生成器;break;
                            case end:worldBuilder = worldConfig.末地界生成器;break;
                        }
                    }
                    try {
                        worldDataLock.setCustomDataByte("WorldCreator",YamlConfig.saveToString(worldBuilder).getBytes(StandardCharsets.UTF_8));
                    } catch (YamlException e) {
                        e.printStackTrace();
                    }
                }
                if (worldBuilder==null){
                    try {
                        worldBuilder = YamlConfig.loadFromString(new String(bytes,StandardCharsets.UTF_8),WorldConfig.WorldBuilder.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        worldBuilder = worldConfig.主世界生成器;
                    }
                }
                playerWorlds.putWorld(s,worldDataLock,worldBuilder.getWordBuilder(worldDataLockNameMap.get(worldDataLock)));
            });
            worldsMap.put(worlds,playerWorlds);
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
        new HashMap<>(nameMap).forEach((s, playerWord) -> unloadPlayerWorlds(playerWord,true));
        nameMap = new HashMap<>();
        wordMap = new HashMap<>();
    }

    /**
     * 删除世界
     */
    public void delPlayerWorlds(Worlds worlds) {
        重新写
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
        form.setPlayerLocation(event.getPlayer(),event.getFrom());
    }
    @EventHandler
    public void 玩家离开服务器(PlayerQuitEvent event){
        PlayerWorlds playerWorlds = getBeLoadPlayerWorlds(event.getPlayer().getWorld());
        if (playerWorlds!=null){
            playerWorlds.setPlayerLocation(event.getPlayer(),event.getPlayer().getLocation());
        }
    }
}
