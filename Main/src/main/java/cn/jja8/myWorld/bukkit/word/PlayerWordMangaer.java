package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.function.Consumer;

/**
 * 用于管理每个世界
 */
public class PlayerWordMangaer implements Listener {
    /**
     * 加载进度接收
     * */
    public static class LoadingProgress implements cn.jja8.myWorld.bukkit.basic.worldDataSupport.LoadingProgress {
        Lang lang = ConfigBukkit.getLang();
        String worldName;
        int v =0;
        long t = 0;
        public LoadingProgress(String worldName) {
            this.worldName = worldName;
        }
        @Override
        public void LoadingProgress(int loading) {
            try {
                if (System.currentTimeMillis()-50<t){
                    return;
                }
                t = System.currentTimeMillis();
                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player ->
                        player.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
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
                            new TextComponent(lang.世界加载完成提示文本)
                    )
            );
        }
    }
    Lang lang = ConfigBukkit.getLang();
    WorldConfig worldConfig =  ConfigBukkit.getWorldConfig();
    Map<World, PlayerWorlds> wordMap = new HashMap<>();
    Map<String, PlayerWorlds> nameMap = new HashMap<>();
    Map<String, PlayerWorlds> loadingMap = new HashMap<>();
    Map<PlayerWorlds, List<Consumer<PlayerWorlds>>> loadedMap = new HashMap<>();

    public PlayerWordMangaer() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this,MyWorldBukkit.getMyWorldBukkit());
    }

    /**
     * 通过世界名称加载一个玩家世界
     * @param name 世界唯一名称。
     * @param consumer 世界加载完成后调用， null 如果世界被其他服务器加载
     */
    public void loadPlayerWorlds(String name,Consumer<PlayerWorlds> consumer){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            //查找有没有已经被加载的
            PlayerWorlds playerWorlds = getBeLoadPlayerWorlds(name);
            if (playerWorlds !=null){
                consumer.accept(playerWorlds);
                return;
            }
            synchronized (PlayerWordMangaer.this){
                playerWorlds = loadingMap.get(name);
                if (playerWorlds!=null){
                    loadedMap.computeIfAbsent(playerWorlds, k -> new ArrayList<>()).add(consumer);
                    return;
                }
                playerWorlds = new PlayerWorlds();
                loadedMap.computeIfAbsent(playerWorlds, k -> new ArrayList<>()).add(consumer);
                loadingMap.put(name,playerWorlds);
            }
            playerWorlds.name = name;
            //验证没被其他服务器加载------
            playerWorlds.锁 = WorldData.worldDataSupport.getWorldDataLock(name);
            if (playerWorlds.锁.isLocked()){
                consumer.accept(null);
                return;
            }
            playerWorlds.锁.locked(worldConfig.服务器名称);
            playerWorlds.playerWordInform = new PlayerWordInform(name);
            MyWorldBukkit.getMyWorldBukkit().getLogger().info("加载"+playerWorlds.getName()+"世界组。");

            //加载世界-------
            {
                LoadingProgress loadingProgress = new LoadingProgress(name);
                playerWorlds.putWorld(PlayerWorldTypeAtName.world,WorldData.worldDataSupport.loadWorldAsync(worldConfig.主世界生成器.getWordBuilder(name), name,loadingProgress));
                loadingProgress.finish();
            }
            //地狱
            if (worldConfig.地狱界生成器.启用){
                String wordName = name+"_nether";
                LoadingProgress loadingProgress = new LoadingProgress(wordName);
                playerWorlds.putWorld(PlayerWorldTypeAtName.infernal,WorldData.worldDataSupport.loadWorldAsync(worldConfig.地狱界生成器.getWordBuilder(wordName),wordName, loadingProgress));
                loadingProgress.finish();
            }
            //末地
            if (worldConfig.末地界生成器.启用){
                String wordName = name+"_the_end";
                LoadingProgress loadingProgress = new LoadingProgress(wordName);
                playerWorlds.putWorld(PlayerWorldTypeAtName.end,WorldData.worldDataSupport.loadWorldAsync(worldConfig.末地界生成器.getWordBuilder(wordName), wordName,loadingProgress));
                loadingProgress.finish();
            }
            for (World value : playerWorlds.worldMap.values()) {
                wordMap.put(value,playerWorlds);
            }
            nameMap.put(name, playerWorlds);
            loadingMap.remove(name);
            List<Consumer<PlayerWorlds>> list= loadedMap.remove(playerWorlds);
            if (list!=null){
                for (Consumer<PlayerWorlds> consumer1 : list) {
                    consumer1.accept(playerWorlds);
                }
            }
        });
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
     public void unloadPlayerWorlds(PlayerWorlds playerWord, boolean save) {
         MyWorldBukkit.getMyWorldBukkit().getLogger().info("卸载"+playerWord.getName()+"世界组。"+(save?"并保存":"并不保存"));
         World world = Bukkit.getWorld(worldConfig.主世界名称);
         Consumer<Player> consumer = player -> {
             if (world==null){
                 player.kickPlayer(lang.世界卸载_找不到主世界.replaceAll("<世界>", worldConfig.主世界名称));
             }else {
                 player.teleport(world.getSpawnLocation());
             }
         };
         for (World value : playerWord.worldMap.values()) {
             for (Player player : value.getPlayers()) {
                 consumer.accept(player);
             }
             WorldData.worldDataSupport.unloadWorld(value,save);
             wordMap.remove(value);
         }
         if (save){
             playerWord.getPlayerWordInform().save();
         }
         nameMap.remove(playerWord.getName());
         playerWord.锁.unlock(worldConfig.服务器名称);
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
        MyWorldBukkit.getMyWorldBukkit().getServer().getScheduler().runTaskAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> {
            WorldData.worldDataSupport.delWorld(worldName);
            WorldData.worldDataSupport.delWorld(worldName+"_nether");
            WorldData.worldDataSupport.delWorld(worldName+"_the_end");
        });
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
