package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.WorldDataLock;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.YamlConfig;
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
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;


/**
 * 代表一个世界
 */
public class PlayerWorlds {

    WorldConfig worldConfig = ConfigBukkit.getWorldConfig();

    PlayerWordInform playerWordInform;
    String name;

    Map<String, WorldDataLock> worldLockMap = new HashMap<>();
    Map<String,World> loadedWorldMap = new HashMap<>();

    public PlayerWorlds(PlayerWordInform playerWordInform, String name) {
        this.playerWordInform = playerWordInform;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public PlayerWordInform getPlayerWordInform() {
        return playerWordInform;
    }
    public World getWorld(String type){
        重新写
    }
    public World getWorld(PlayerWorldTypeAtName type) {
        重新写
    }
    public void putWorld(String type, WorldDataLock world, WorldCreator worldCreator){
        Bukkit.getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
           World world1 = world.loadWorldAsync(worldCreator,new LoadingProgress(type));
           worldLockMap.put(type,world);
           loadedWorldMap.put(type,world1);
        });
    }
    public void putWorld(PlayerWorldTypeAtName type,WorldDataLock world){
        byte[] bytes = world.getCustomDataByte("worldCreator");
        WorldCreator worldCreator = null;
        if (bytes!=null) {
            try {
                worldCreator = YamlConfig.loadFromString(new String(bytes, StandardCharsets.UTF_8),WorldConfig.WorldBuilder.class).getWordBuilder(type.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (worldCreator==null) {
            switch (type){
                case world:worldCreator=worldConfig.主世界生成器.getWordBuilder(type.toString());break;
                case end:worldCreator=worldConfig.末地界生成器.getWordBuilder(type.toString());break;
                case infernal:worldCreator=worldConfig.地狱界生成器.getWordBuilder(type.toString());break;
            }
        }
        putWorld(type.toString(),world,worldCreator);
    }
    public void setPlayerLocation(Player player, Location location) {
        重新写
    }
    /**
     * @return null 没有这个玩家的位置
     * */
    public Location getPlayerLocation(Player player) {
        重新写
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
            for (World s : worldLockMap.values()) {
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
