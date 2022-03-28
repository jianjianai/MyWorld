package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.all.basic.DatasheetSupport.Worlds;
import cn.jja8.myWorld.all.basic.DatasheetSupport.WorldsData;
import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.patronSaint_2022_3_2_1244.bukkit.Data.String.LocationToString;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
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
    Worlds worlds;

    Map<World, WorldDataLock> worldLockMap;
    Map<String,World> typeWorldMap;

    public PlayerWorlds(PlayerWordManager playerWordManager, PlayerWordInform playerWordInform, String name,Worlds worlds,Map<World, WorldDataLock> worldLockMap,Map<String,World> typeWorldMap) {
        this.playerWordManager = playerWordManager;
        this.playerWordInform = playerWordInform;
        this.name = name;
        this.worlds = worlds;
        this.worldLockMap = worldLockMap;
        this.typeWorldMap = typeWorldMap;
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
        playerWordManager.worldsMap.remove(worlds);
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
     * 不允许在主线程调用，会柱塞线程
     * */
    public World putWorld(String type, WorldDataLock world, WorldConfig.WorldBuilder worldCreator){
        World world1;
        LoadingProgress loadingProgress =  new LoadingProgress(worldCreator.name());
        synchronized (PlayerWorlds.class){
             world1 = world.loadWorldAsync(worldCreator,loadingProgress);
        }
        loadingProgress.finish();
        worldLockMap.put(world1,world);
        typeWorldMap.put(type,world1);
        playerWordManager.wordMap.put(world1,this);
        return world1;
    }
    /**
     * 不允许在主线程调用，会柱塞线程
     * */
    public World putWorld(PlayerWorldTypeAtName type,WorldDataLock world, WorldConfig.WorldBuilder worldCreator){
        return putWorld(type.toString(),world,worldCreator);
    }
    public void setPlayerLeaveLocation(Player player, Location location) {
        String dataName = "location/"+player.getUniqueId();
        WorldsData worldsData = worlds.getWorldsData(dataName);
        if (worldsData==null){
            worldsData = worlds.newWorldsData(dataName);
        }
        worldsData.setData(LocationToString.totring(location).getBytes(StandardCharsets.UTF_8));
    }
    /**
     * @return null 没有这个玩家的位置
     * */
    public Location getPlayerLocation(Player player) {
        String dataName = "location/"+player.getUniqueId();
        WorldsData worldsData = worlds.getWorldsData(dataName);
        if (worldsData==null){
            return null;
        }
        return LocationToString.load(new String(worldsData.getData(),StandardCharsets.UTF_8));
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
