package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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
    public static class worldBusy extends Error {}
    /**
     * 用于发送世界加载信息给全部玩家，有助于防止掉线
     * */
    public static class LoadWorldsNews{
        private String loadNews = "null";
        private int x = 0;
        private boolean task = true;
        public LoadWorldsNews() {
            //bukkit的异步任务在主线程被阻塞的情况下不会运行，所以就用Thread
            new Thread(() -> {
                while (task){
                    x++;
                    MyWorldBukkit.getMyWorldBukkit().getServer().getOnlinePlayers().forEach((Consumer<Player>) player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(loadNews.replaceAll("<数>", String.valueOf(x)))));
                    try { Thread.sleep(50); } catch (InterruptedException ignored) { }
                }
            }).start();
        }
        public void setLoadWold(String loadNews) {
            this.loadNews = loadNews;
            x =0;
        }
        public void finish(String finishNews) {
            task = false;
            MyWorldBukkit.getMyWorldBukkit().getServer().getOnlinePlayers().forEach((Consumer<Player>) player -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(finishNews)));
        }
    }
    boolean worldBusy = false;
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
    public PlayerWorlds loadPlayerWorlds(String name) throws worldBusy{
        if (worldBusy){
            throw new worldBusy();
        }
        worldBusy =true;
        //查找有没有已经被加载的
        PlayerWorlds playerWorlds = getBeLoadPlayerWorlds(name);
        if (playerWorlds !=null){
            worldBusy =false;
            return playerWorlds;
        }
        playerWorlds = new PlayerWorlds();
        playerWorlds.name = name;
        //验证没被其他服务器加载------
        playerWorlds.锁 = WorldData.worldDataSupport.getWorldDataLock(name);
        if (playerWorlds.锁.isLocked()){
            worldBusy=false;
            return null;
        }
        playerWorlds.锁.locked(MyWorldBukkit.getWorldConfig().服务器名称);
        playerWorlds.世界信息 = new PlayerWordInform(name);
        MyWorldBukkit.getMyWorldBukkit().getLogger().info("加载"+playerWorlds.getName()+"世界组。");
        LoadWorldsNews loadWorldsNews = new LoadWorldsNews();
        try {
            //加载世界-------
            {
                loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界加载提示文本.replaceAll("<世界>",name));
                WorldCreator 世界生成器 = new WorldCreator(name);
                世界生成器.generateStructures(MyWorldBukkit.getWorldConfig().主世界生成器.生成建筑);
                世界生成器.environment(MyWorldBukkit.getWorldConfig().主世界生成器.世界维度);
                世界生成器.type(MyWorldBukkit.getWorldConfig().主世界生成器.世界类型);
                世界生成器.generator(WorldCreator.getGeneratorForName(name, MyWorldBukkit.getWorldConfig().主世界生成器.世界生成器,null));
                世界生成器.generatorSettings(MyWorldBukkit.getWorldConfig().主世界生成器.世界生成器参数);
                playerWorlds.主世界 = WorldData.worldDataSupport.loadWorld(世界生成器,name);
            }
            //地狱
            if (MyWorldBukkit.getWorldConfig().地狱界生成器.启用){
                String wordName = name+"_nether";
                loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界加载提示文本.replaceAll("<世界>",wordName));
                WorldCreator 世界生成器 = new WorldCreator(wordName);
                世界生成器.generateStructures(MyWorldBukkit.getWorldConfig().地狱界生成器.生成建筑);
                世界生成器.environment(MyWorldBukkit.getWorldConfig().地狱界生成器.世界维度);
                世界生成器.type(MyWorldBukkit.getWorldConfig().地狱界生成器.世界类型);
                世界生成器.generator(WorldCreator.getGeneratorForName(name, MyWorldBukkit.getWorldConfig().地狱界生成器.世界生成器,null));
                世界生成器.generatorSettings(MyWorldBukkit.getWorldConfig().地狱界生成器.世界生成器参数);
                playerWorlds.地狱 = WorldData.worldDataSupport.loadWorld(世界生成器,wordName);
            }
            //末地
            if (MyWorldBukkit.getWorldConfig().末地界生成器.启用){
                String wordName = name+"_the_end";
                loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界加载提示文本.replaceAll("<世界>",wordName));
                WorldCreator 世界生成器 = new WorldCreator(wordName);
                世界生成器.generateStructures(MyWorldBukkit.getWorldConfig().末地界生成器.生成建筑);
                世界生成器.environment(MyWorldBukkit.getWorldConfig().末地界生成器.世界维度);
                世界生成器.type(MyWorldBukkit.getWorldConfig().末地界生成器.世界类型);
                世界生成器.generator(WorldCreator.getGeneratorForName(name, MyWorldBukkit.getWorldConfig().末地界生成器.世界生成器,null));
                世界生成器.generatorSettings(MyWorldBukkit.getWorldConfig().末地界生成器.世界生成器参数);
                playerWorlds.末地 = WorldData.worldDataSupport.loadWorld(世界生成器, wordName);
            }
        }catch (Exception|Error e){
            loadWorldsNews.finish(MyWorldBukkit.getLang().世界加载完成提示文本);
            worldBusy =false;
            throw e;
        }
        loadWorldsNews.finish(MyWorldBukkit.getLang().世界加载完成提示文本);
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
        worldBusy =false;
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
     public void unloadPlayerWorlds(PlayerWorlds playerWord, boolean save) throws worldBusy{
         if (worldBusy){
             throw new worldBusy();
         }
         worldBusy =true;
         MyWorldBukkit.getMyWorldBukkit().getLogger().info("卸载"+playerWord.getName()+"世界组。"+(save?"并保存":"并不保存"));
         World world = Bukkit.getWorld(MyWorldBukkit.getWorldConfig().主世界名称);
         Consumer<Player> consumer = player -> {
             if (world==null){
                 player.kickPlayer(MyWorldBukkit.getLang().世界卸载_找不到主世界.replaceAll("<世界>", MyWorldBukkit.getWorldConfig().主世界名称));
             }else {
                 player.teleport(world.getSpawnLocation());
             }
         };
         LoadWorldsNews loadWorldsNews = new LoadWorldsNews();
         try {
             if (playerWord.getWorld()!=null){
                 playerWord.getWorld().getPlayers().forEach(consumer);
                 loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界卸载提示文本.replaceAll("<世界>",playerWord.getWorld().getName()));
                 WorldData.worldDataSupport.unloadWorld(playerWord.getWorld(),save);
             }
             if (playerWord.getInfernalWorld()!=null){
                 playerWord.getInfernalWorld().getPlayers().forEach(consumer);
                 loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界卸载提示文本.replaceAll("<世界>",playerWord.getInfernalWorld().getName()));
                 WorldData.worldDataSupport.unloadWorld(playerWord.getInfernalWorld(),save);
             }
             if (playerWord.getEndWorld()!=null){
                 playerWord.getEndWorld().getPlayers().forEach(consumer);
                 loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界卸载提示文本.replaceAll("<世界>",playerWord.getEndWorld().getName()));
                 WorldData.worldDataSupport.unloadWorld(playerWord.getEndWorld(),save);
             }
             if (save){
                 loadWorldsNews.setLoadWold(MyWorldBukkit.getLang().世界卸载提示文本.replaceAll("<世界>",playerWord.getName()+".save"));
                 playerWord.getPlayerWordInform().save();
             }
         }catch (Exception|Error e){
             loadWorldsNews.finish(MyWorldBukkit.getLang().世界完成卸载提示文本);
             throw e;
         }
         loadWorldsNews.finish(MyWorldBukkit.getLang().世界完成卸载提示文本);
         wordMap.remove(playerWord.getWorld());
         wordMap.remove(playerWord.getEndWorld());
         wordMap.remove(playerWord.getInfernalWorld());
         nameMap.remove(playerWord.getName());
         playerWord.锁.unlock(MyWorldBukkit.getWorldConfig().服务器名称);
         worldBusy =false;
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
}
