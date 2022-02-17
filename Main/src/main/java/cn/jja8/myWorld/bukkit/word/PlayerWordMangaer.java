package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 用于管理每个世界
 */
public class PlayerWordMangaer implements Listener {
    Map<World, PlayerWorlds> wordMap = new HashMap<>();
    Map<String, PlayerWorlds> nameMap = new HashMap<>();

    public PlayerWordMangaer() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this,MyWorldBukkit.getMyWorldBukkit());
    }

    /**
     * 通过世界名称加载一个玩家世界
     * @param name 世界唯一名称。
     * @return null 如果世界被其他服务器加载
     */
     public PlayerWorlds loadPlayerWorlds(String name){
         PlayerWorlds playerWorlds = nameMap.get(name);
         if (playerWorlds !=null){
             return playerWorlds;
         }
         try {
             playerWorlds = new PlayerWorlds(name);
         }catch (PlayerWorlds.LoadedByAnotherServer r){
             return null;
         }
         nameMap.put(name, playerWorlds);
         if (playerWorlds.getWorld()!=null){
             wordMap.put(playerWorlds.getWorld(), playerWorlds);
         }
         if (playerWorlds.getInfernalWorld()!=null){
             wordMap.put(playerWorlds.getInfernalWorld(), playerWorlds);
         }
         if (playerWorlds.getEndWorld()!=null){
             wordMap.put(playerWorlds.getEndWorld(), playerWorlds);
         }
         return playerWorlds;
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
     * 卸载一个玩家世界
     */
     public void unloadPlayerWorlds(PlayerWorlds playerWord, boolean save){
         MyWorldBukkit.getMyWorldBukkit().getLogger().info("卸载"+playerWord.getName()+"世界组。"+(save?"并保存":"并不保存"));
         World world = Bukkit.getWorld(MyWorldBukkit.getWorldConfig().主世界名称);
         Consumer<Player> consumer = player -> {
             if (world==null){
                 player.kickPlayer(MyWorldBukkit.getLang().世界卸载_找不到主世界.replaceAll("<世界>", MyWorldBukkit.getWorldConfig().主世界名称));
                 return;
             }
             player.teleport(world.getSpawnLocation());
         };
         if (playerWord.getWorld()!=null){
             playerWord.getWorld().getPlayers().forEach(consumer);
             WorldData.worldDataSupport.unloadWorld(playerWord.getWorld(),save);
         }
         if (playerWord.getInfernalWorld()!=null){
             playerWord.getInfernalWorld().getPlayers().forEach(consumer);
             WorldData.worldDataSupport.unloadWorld(playerWord.getInfernalWorld(),save);
         }
         if (playerWord.getEndWorld()!=null){
             playerWord.getEndWorld().getPlayers().forEach(consumer);
             WorldData.worldDataSupport.unloadWorld(playerWord.getEndWorld(),save);
         }
         if (save){
             playerWord.getPlayerWordInform().save();
         }
         wordMap.remove(playerWord.getWorld());
         wordMap.remove(playerWord.getEndWorld());
         wordMap.remove(playerWord.getInfernalWorld());
         nameMap.remove(playerWord.getName());
         playerWord.锁.unlock(MyWorldBukkit.getWorldConfig().服务器名称);
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
        if (isWorldExistence(name)){
            return new PlayerWordInform(name);
        }
        return null;
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
        //先卸载，再删除
        PlayerWorlds sj = getBeLoadPlayerWorlds(worldName);
        if (sj!=null){
            unloadPlayerWorlds(sj,false);
        }
        WorldData.worldDataSupport.delWorld(worldName);
        WorldData.worldDataSupport.delWorld(worldName+"_nether");
        WorldData.worldDataSupport.delWorld(worldName+"_the_end");
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
}
