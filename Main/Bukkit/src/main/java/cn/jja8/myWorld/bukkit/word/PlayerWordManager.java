package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.patronSaint_2022_3_2_1244.allUsed.file.YamlConfig;
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
import java.util.function.Consumer;

/**
 * 用于管理每个世界
 */
public class PlayerWordManager implements Listener {

    Lang lang = ConfigBukkit.getLang();
    WorldConfig worldConfig =  ConfigBukkit.getWorldConfig();

    Map<World, PlayerWorlds> wordMap = new HashMap<>();
    Map<String, PlayerWorlds> nameMap = new HashMap<>();
    Map<String, PlayerWorlds> loadingMap = new HashMap<>();
    Map<PlayerWorlds, List<Consumer<PlayerWorlds>>> loadedMap = new HashMap<>();

    public PlayerWordManager() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this,MyWorldBukkit.getMyWorldBukkit());
    }


    /**
     * 通过世界名称加载一个玩家世界
     * @param name 世界唯一名称。
     * @param consumer 世界加载完成后调用， null 如果世界被其他服务器加载
     */
    public void loadPlayerWorlds(String name,Consumer<PlayerWorlds> consumer){
        重新写
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
    public PlayerWorlds getBeLoadPlayerWorlds(String PlayerWorldName){
        return nameMap.get(PlayerWorldName);
    }

    /**
     * 被加载的世界名称集合
     * */
    public Set<String> getWorldNames(){
        return nameMap.keySet();
    }
    /**
     * 获取被加载的世界集合
     * */
    public Collection<PlayerWorlds> getWorlds(){
        return nameMap.values();
    }

    /**
     * 卸载一个玩家世界
     */
     public void unloadPlayerWorlds(PlayerWorlds playerWord, boolean save) {
         重新写
     }

    /**
     * 判断这个世界是否存在
     */
    public boolean isWorldExistence(String name){
        return WorldData.worldDataSupport.isWorldExistence(name);
    }
    /**
     * 获取某个世界的信息.
     * @return 如果世界不存在就返回null
     */
    public PlayerWordInform getPlayerWordInform(String name){
        重新写
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
    public void delPlayerWorlds(String worldName) {
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
