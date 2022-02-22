package cn.jja8.myWorld.bukkit.word;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.WorldData;
import cn.jja8.myWorld.bukkit.basic.worldDataSupport.RunOnCompletion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.function.Consumer;

/**
 * 用于管理每个世界
 */
public class PlayerWordMangaer implements Listener {
    public class Inspect{
        List<WorldRunOnCompletion> worldRunOnCompletionList;
        Runnable run;

        public Inspect(List<WorldRunOnCompletion> worldRunOnCompletionList, Runnable run) {
            this.worldRunOnCompletionList = worldRunOnCompletionList;
            this.run = run;
            for (WorldRunOnCompletion worldRunOnCompletion : this.worldRunOnCompletionList) {
                worldRunOnCompletion.inspect = this;
            }
        }

        private void finish(WorldRunOnCompletion worldRunOnCompletion){
            worldRunOnCompletionList.remove(worldRunOnCompletion);
            if (run==null){
                return;
            }
            if (worldRunOnCompletionList.size()<1){
                run.run();
                run=null;
            }
        }
    }
    public class WorldRunOnCompletion implements RunOnCompletion{
        private Inspect inspect;
        private PlayerWorlds playerWorlds;
        private PlayerWorlds.WorldType worldType;
        private String name;

        public WorldRunOnCompletion(PlayerWorlds playerWorlds, PlayerWorlds.WorldType worldType,String name) {
            this.playerWorlds = playerWorlds;
            this.worldType = worldType;
            this.name = name;
        }

        /**
         * 加载完成后执行
         *
         * @param world
         */
        @Override
        public void CompletionRun(World world) {
            playerWorlds.putWorld(worldType,world);
            wordMap.put(world, playerWorlds);
            if (inspect!=null){
                inspect.finish(this);
            }
        }

        /**
         * 返回世界加载进度
         * @param loading
         */
        @Override
        public void LoadingProgress(int loading) {
            Bukkit.getLogger().info("正在加载世界'"+name+"'.."+loading+"%");
        }
    }
    Map<World, PlayerWorlds> wordMap = new HashMap<>();
    Map<String, PlayerWorlds> nameMap = new HashMap<>();

    public PlayerWordMangaer() {
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this,MyWorldBukkit.getMyWorldBukkit());
    }

    /**
     * 通过世界名称加载一个玩家世界
     * @param name 世界唯一名称。
     * @param consumer 世界加载完成后调用， null 如果世界被其他服务器加载
     */
    public void loadPlayerWorlds(String name,Consumer<PlayerWorlds> consumer){
        //查找有没有已经被加载的
        PlayerWorlds playerWorlds = getBeLoadPlayerWorlds(name);
        if (playerWorlds !=null){
            consumer.accept(playerWorlds);
            return;
        }
        playerWorlds = new PlayerWorlds();
        nameMap.put(name, playerWorlds);
        playerWorlds.name = name;
        //验证没被其他服务器加载------
        playerWorlds.锁 = WorldData.worldDataSupport.getWorldDataLock(name);
        if (playerWorlds.锁.isLocked()){
            consumer.accept(null);
            return;
        }
        playerWorlds.锁.locked(MyWorldBukkit.getWorldConfig().服务器名称);
        playerWorlds.playerWordInform = new PlayerWordInform(name);
        MyWorldBukkit.getMyWorldBukkit().getLogger().info("加载"+playerWorlds.getName()+"世界组。");

        List<WorldRunOnCompletion> worldRunOnCompletionList = new ArrayList<>();
        List<Runnable> runnableList = new ArrayList<>();
        //加载世界-------
        {
            WorldRunOnCompletion worldRunOnCompletion = new WorldRunOnCompletion(playerWorlds, PlayerWorlds.WorldType.world,name);
            worldRunOnCompletionList.add(worldRunOnCompletion);
            runnableList.add(() -> WorldData.worldDataSupport.loadWorldAsync(MyWorldBukkit.getWorldConfig().主世界生成器.getWordBuilder(name), name,worldRunOnCompletion));

        }
        //地狱
        if (MyWorldBukkit.getWorldConfig().地狱界生成器.启用){
            String wordName = name+"_nether";
            WorldRunOnCompletion worldRunOnCompletion = new WorldRunOnCompletion(playerWorlds, PlayerWorlds.WorldType.infernal,wordName);
            worldRunOnCompletionList.add(worldRunOnCompletion);
            runnableList.add(() -> WorldData.worldDataSupport.loadWorldAsync(MyWorldBukkit.getWorldConfig().地狱界生成器.getWordBuilder(wordName), wordName,worldRunOnCompletion));
        }
        //末地
        if (MyWorldBukkit.getWorldConfig().末地界生成器.启用){
            String wordName = name+"_the_end";
            WorldRunOnCompletion worldRunOnCompletion = new WorldRunOnCompletion(playerWorlds, PlayerWorlds.WorldType.end,wordName);
            worldRunOnCompletionList.add(worldRunOnCompletion);
            runnableList.add(() -> WorldData.worldDataSupport.loadWorldAsync(MyWorldBukkit.getWorldConfig().末地界生成器.getWordBuilder(wordName), wordName,worldRunOnCompletion));
        }

        PlayerWorlds finalPlayerWorlds = playerWorlds;
        new Inspect(worldRunOnCompletionList, () -> consumer.accept(finalPlayerWorlds));
        for (Runnable runnable : runnableList) {
            runnable.run();
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
    public PlayerWorlds getBeLoadPlayerWorlds(String PlayerWorldName){
        return nameMap.get(PlayerWorldName);
    }

    /**
     * 卸载一个玩家世界
     */
     public void unloadPlayerWorlds(PlayerWorlds playerWord, boolean save) {
         MyWorldBukkit.getMyWorldBukkit().getLogger().info("卸载"+playerWord.getName()+"世界组。"+(save?"并保存":"并不保存"));
         World world = Bukkit.getWorld(MyWorldBukkit.getWorldConfig().主世界名称);
         Consumer<Player> consumer = player -> {
             if (world==null){
                 player.kickPlayer(MyWorldBukkit.getLang().世界卸载_找不到主世界.replaceAll("<世界>", MyWorldBukkit.getWorldConfig().主世界名称));
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
