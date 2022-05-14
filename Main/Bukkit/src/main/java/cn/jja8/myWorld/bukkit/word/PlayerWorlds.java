package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroup;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldGroupData;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.WorldBuilder;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.myWorld.bukkit.word.error.ExistsType;
import cn.jja8.myWorld.bukkit.word.error.ExistsWorld;
import cn.jja8.myWorld.bukkit.word.error.NoAllWorldLocks;
import cn.jja8.myWorld.bukkit.work.error.NoWorldLocks;
import cn.jja8.myWorld.bukkit.word.name.PlayerWorldTypeAtName;
import cn.jja8.myWorld.bukkit.word.name.WorldCustomDataName;
import cn.jja8.myWorld.bukkit.word.name.WorldsDataName;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.Data.String.LocationToString;
import com.esotericsoftware.yamlbeans.YamlException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * 代表一个世界
 */
public class PlayerWorlds {

    WorldConfig worldConfig = ConfigBukkit.getWorldConfig();
    Lang lang = ConfigBukkit.getLang();

    PlayerWordManager playerWordManager;
    PlayerWordInform playerWordInform;
    String name;
    WorldGroup worldGroup;

    Map<World, WorldDataLock> worldLockMap = new HashMap<>();
    Map<String,World> typeWorldMap=new HashMap<>();

    /**
     * 获取type与world的对应表
     * */
    public Map<String, World> getTypeWorldMap() {
        return new HashMap<>(typeWorldMap);
    }

    /**
     * 请在异步调用，有可能阻塞线程
     * */
    public PlayerWorlds(PlayerWordManager playerWordManager, WorldGroup worldGroup) throws NoAllWorldLocks {
        this.playerWordManager = playerWordManager;
        this.worldGroup = worldGroup;
        this.name = worldGroup.getWorldGroupName();

        WorldGroupData worldGroupData = worldGroup.getWorldGroupData(WorldsDataName.playerWordInform.toString());
        if (worldGroupData ==null){
            worldGroupData = worldGroup.newWorldGroupData(WorldsDataName.playerWordInform.toString());
        }
        playerWordInform = new PlayerWordInform(worldGroupData);
        List<String> worldList = worldGroup.getWorldList();
        Map<WorldDataLock,String> worldDataLockNameMap = new HashMap<>();
        //加载所有世界的锁
        for (String s : worldList) {
            WorldCreator worldCreator = new WorldCreator(s);
            WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldCreator,worldConfig.服务器名称);
            if (worldDataLock!=null) {
                worldDataLockNameMap.put(worldDataLock,s);

                //加载世界生成器
                WorldBuilder worldBuilder = null;
                byte[] bytes = worldDataLock.getCustomDataByte(WorldCustomDataName.WorldCreator.toString());
                if (bytes!=null){
                    try {
                        worldBuilder = WorldBuilder.loadAsByte(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (worldBuilder==null){
                    worldBuilder = worldConfig.主世界生成器;
                }
                worldCreator.copy(worldBuilder.getWordBuilder(s));
            }
        }
        //判断世界全部上锁
        if (worldDataLockNameMap.size()<worldList.size()){
            for (WorldDataLock worldDataLock : worldDataLockNameMap.keySet()) {
                worldDataLock.unlock();
            }
            throw new NoAllWorldLocks("无法获取"+name+"世界组中全部的锁。");
        }

        //加载全部世界
        worldDataLockNameMap.forEach((worldDataLock, worldName) -> {
            byte[] bytes = worldDataLock.getCustomDataByte(WorldCustomDataName.WorldType.toString());
            if (bytes==null){
                MyWorldBukkit.getMyWorldBukkit().getLogger().warning(name+"中"+worldName+"世界没有世界类型，将不会被加载。");
                worldDataLock.unlock();
                return;
            }
            String worldType = new String(bytes,StandardCharsets.UTF_8);
            LoadingProgress loadingProgress =  new LoadingProgress(worldName);
            World world = worldDataLock.loadWorldAsync(loadingProgress);
            loadingProgress.finish();
            typeWorldMap.put(worldType,world);
            worldLockMap.put(world,worldDataLock);
            playerWordManager.wordMap.put(world,this);
        });
        playerWordManager.worldsMap.put(worldGroup,this);
    }

    /**
     * 卸载全部世界，并释放资源
     * */
    public void unLoad(boolean save){
        World mainWord = Bukkit.getWorld(worldConfig.主世界名称);
        if (mainWord==null){
            typeWorldMap.forEach((worldType, world) -> {
                for (Player player : world.getPlayers()) {
                    player.kickPlayer(lang.世界卸载主世界配置错误);
                }
                WorldDataLock worldDataLock = worldLockMap.get(world);
                worldDataLock.unloadWorld(save);
                worldDataLock.setCustomDataByte("WorldType",worldType.getBytes(StandardCharsets.UTF_8));
                worldDataLock.unlock();
                playerWordManager.wordMap.remove(world);
            });
        }else {
            typeWorldMap.forEach((worldType, world) -> {
                for (Player player : world.getPlayers()) {
                    player.teleport(mainWord.getSpawnLocation());
                }
                WorldDataLock worldDataLock = worldLockMap.get(world);
                worldDataLock.unloadWorld(save);
                worldDataLock.setCustomDataByte("WorldType",worldType.getBytes(StandardCharsets.UTF_8));
                worldDataLock.unlock();
                playerWordManager.wordMap.remove(world);
            });
        }
        playerWordManager.worldsMap.remove(worldGroup);
        worldLockMap=null;
        typeWorldMap=null;
        if (save) {
            playerWordInform.save();
        }
        playerWordInform = null;
    }

    public String getName() {
        return name;
    }
    public PlayerWordInform getPlayerWordInform() {
        return playerWordInform;
    }
    public World getWorld(String type){
        return typeWorldMap.get(type);
    }
    public World getWorld(PlayerWorldTypeAtName type) {
        return getWorld(type.toString());
    }
    /**
     * 不允许在主线程调用，会柱塞线程。需要确保worldCreator的名称不和任何世界重名。type同一个世界组中不允许重名。
     * */
    public World putWorld(String type, WorldBuilder worldBuilder, String WorldName) throws ExistsWorld, NoWorldLocks, ExistsType {
        if (WorldData.worldDataSupport.isWorldExistence(WorldName)){
            throw new ExistsWorld("世界已经被创建");
        }
        WorldDataLock worldDataLock = WorldData.worldDataSupport.getWorldDataLock(worldBuilder.getWordBuilder(WorldName),worldConfig.服务器名称);
        if (worldDataLock==null){
            throw new NoWorldLocks("无法获得锁");
        }
        World world1;
        LoadingProgress loadingProgress =  new LoadingProgress(WorldName);
        synchronized (PlayerWorlds.class){
            if (typeWorldMap.containsKey(type)){
                worldDataLock.unlock();
                loadingProgress.finish();
                throw new ExistsType("type已经存在");
            }
            byte[] data = new byte[0];
            try {
                data = worldBuilder.saveToByte();
            } catch (YamlException e) {
                e.printStackTrace();
            }
            if (data!=null){
                worldDataLock.setCustomDataByte(WorldCustomDataName.WorldCreator.toString(),data);
            }
            if (!worldGroup.containsWorld(WorldName)){
                worldGroup.addWorld(WorldName);
            }
            world1 = worldDataLock.loadWorldAsync(loadingProgress);
            worldLockMap.put(world1,worldDataLock);
            typeWorldMap.put(type,world1);
            playerWordManager.wordMap.put(world1,this);
        }
        loadingProgress.finish();
        return world1;
    }
    /**
     * 不允许在主线程调用，会柱塞线程。需要确保worldCreator的名称不和任何世界重名。type同一个世界组中不允许重名。
     * */
    public World putWorld(PlayerWorldTypeAtName type, WorldBuilder worldBuilder, String WorldName) throws ExistsType, NoWorldLocks, ExistsWorld {
        return putWorld(type.toString(),worldBuilder,WorldName);
    }
    /**
     * 删除世界
     * */
    public void removeWorld(String worldName){
        if (!worldGroup.containsWorld(worldName)){
            return;
        }
        World world;
        if ((world=typeWorldMap.get(worldName))==null){
            worldGroup.removeWorld(worldName);
            return;
        }
        WorldDataLock worldDataLock = worldLockMap.get(world);
        if (worldDataLock!=null){
            worldDataLock.delWorld();
            worldDataLock.unlock();

        }
        typeWorldMap.remove(worldName);
        worldLockMap.remove(world);
    }




    public void setPlayerLeaveLocation(Player player, Location location) {
        String dataName = "location/"+player.getUniqueId();
        WorldGroupData worldGroupData = worldGroup.getWorldGroupData(dataName);
        if (worldGroupData ==null){
            worldGroupData = worldGroup.newWorldGroupData(dataName);
        }
        worldGroupData.setData(LocationToString.totring(location).getBytes(StandardCharsets.UTF_8));
    }
    /**
     * @return null 没有这个玩家的位置
     * */
    public Location getPlayerLocation(Player player) {
        String dataName = "location/"+player.getUniqueId();
        WorldGroupData worldGroupData = worldGroup.getWorldGroupData(dataName);
        if (worldGroupData ==null){
            return null;
        }
        return LocationToString.load(new String(worldGroupData.getData(),StandardCharsets.UTF_8));
    }

    /**
     * 将某玩家会到这个世界
     * */
    public void playerBack(Player player){
        Location location = null;
        try {
            location = getPlayerLocation(player);
        }catch (Exception|Error e){
            new Exception("玩家"+player.getName()+"在"+name+"世界上次的位置加载失败！",e).printStackTrace();
        }

        if (location!=null){
            player.teleport(location);
        }else {
            playerBackSpawn(player);
        }
    }
    /**
     * 将某玩家传送去出生点
     * */
    public void playerBackSpawn(Player player){
        World world = getWorld(PlayerWorldTypeAtName.world);
        if (world==null){
            for (World s : typeWorldMap.values()) {
                world = s;
                break;
            }
        }
        if (world==null){
            player.sendMessage("你的世界组中没有任何世界，请联系管理员！");
            throw new Error("至少要开启一个世界才能去玩家的世界。");
        }
        player.teleport(world.getSpawnLocation());
    }



    /**
     * 加载进度接收
     * */
    public static class LoadingProgress implements cn.jja8.myWorld.bukkit.basic.worldDataSupport.LoadingProgress {
        UUID uuid = UUID.randomUUID();
        Lang lang = ConfigBukkit.getLang();
        String worldName;
        int v =0;
        long t = 0;
        public LoadingProgress(String worldName) {
            this.worldName = worldName;
            loadingProgress(-1);

        }
        @Override
        public void loadingProgress(int loading) {
            try {
                if (System.currentTimeMillis()-50<t){
                    return;
                }
                t = System.currentTimeMillis();
                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                        player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                uuid,
                                new TextComponent(lang.世界加载提示文本.replaceAll("<世界>",worldName).replaceAll("<数>",loading==-1|loading==0?v():loading+"%"))
                        )
                );
            }catch (Exception|Error throwable){
                throwable.printStackTrace();
            }
        }
        private String v(){
            String s = "/";
            switch (v++%4){
                case 0: s="/";break;
                case 1: s="-";break;
                case 2: s="\\\\";break;
                case 3: s="|";break;
            }
            return s;
        }
        public void finish() {
            Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                    player.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            uuid,
                            new TextComponent(lang.世界加载完成提示文本)
                    )
            );
        }
    }
}
