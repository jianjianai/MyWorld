package cn.jja8.myWorld.bukkit.player;

import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.PlayerData;
import cn.jja8.myWorld.bukkit.basic.playerDataSupport.PlayerDataLock;
import cn.jja8.myWorld.bukkit.word.PlayerWorlds;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 主要负责管理玩家的数据加载和保存
 * 包括玩家背包，玩家下线时的位置。
 * ”玩家数据“字段负责玩家数据的存储，如果需要使用其他保存方式可以在load阶段对他赋值。
 * */
public class PlayerDataManager implements Listener {
    //玩家和锁map
    Map<Player, PlayerDataLock> playerLockMap = new HashMap<>();
    //玩家和加载任务map
    Map<Player, BukkitRunnable> playerLoadRunMap = new HashMap<>();
    //玩家加载完成后运行队列
    Map<Player, Queue<Runnable>> playerLoadFinishedToRunMap = new HashMap<>();
    public PlayerDataManager() {
        //事件监听器
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
        //自动保存
        MyWorldBukkit.getMyWorldBukkit().getServer().getScheduler().runTaskTimerAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> new ArrayList<>(playerLockMap.keySet()).forEach(player -> PlayerData.playerDataSupport.saveData(player)), 20L * MyWorldBukkit.getFileConfig().自动保存时间, 20L * MyWorldBukkit.getFileConfig().自动保存时间);
    }
    /**
     * 如果玩家没有加载完成，就加载完成后执行。如果已经加载完成就立即执行
     * */
    public void playerLoadFinishedToRun(Player player, Runnable runnable){
        if (playerLoadRunMap.get(player)==null){
            runnable.run();
        }else {
            Queue<Runnable> queue = playerLoadFinishedToRunMap.get(player);
            if (queue==null){
                queue = new ArrayDeque<>();
                playerLoadFinishedToRunMap.put(player,queue);
            }
            queue.add(runnable);
        }
    }

    @EventHandler
    public void 玩家离开服务器(PlayerQuitEvent event){
        playerLoadFinishedToRunMap.remove(event.getPlayer());
        //取消掉玩家数据加载任务
        BukkitRunnable rw = playerLoadRunMap.remove(event.getPlayer());
        if (rw!=null){
            if (!rw.isCancelled()){
                rw.cancel();
            }
        }

        //如果有成功获得锁就保存数据，并解锁。
        PlayerDataLock lock = playerLockMap.remove(event.getPlayer());
        if (lock==null){
            return;
        }

        //记住玩家位置
        PlayerWorlds playerWord = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(event.getPlayer().getWorld());
        if (playerWord==null){//如果不在，玩家世界下线
            if (MyWorldBukkit.getPlayerDataConfig().保存玩家非玩家世界位置){
                保存位置(event.getPlayer(),event.getPlayer().getLocation());
            }
        }else {//在玩家世界下
            playerWord.savePlayerLocation(event.getPlayer().getName(),event.getPlayer().getLocation());
        }

        //传送到主世界出生点，防止下次进入服务器时在其他世界，加载数据后产生客户端位置不更新的bug。
        World mainWorld = Bukkit.getWorld(MyWorldBukkit.getWorldConfig().主世界名称);
        if (mainWorld==null){
            MyWorldBukkit.getMyWorldBukkit().getLogger().warning("没有找到主世界"+MyWorldBukkit.getWorldConfig().主世界名称+"，这可能会造成一些bug，请在WorldConfig文件中将主世界设置正确！");
        }else {
            //传送
            event.getPlayer().teleport(mainWorld.getSpawnLocation());
        }

        //保存玩家背包，然后解锁
        PlayerData.playerDataSupport.saveData(event.getPlayer());
        lock.unlock(MyWorldBukkit.getWorldConfig().服务器名称);

    }
    @EventHandler
    public void 玩家进入服务器(PlayerJoinEvent event){
        if (MyWorldBukkit.getPlayerDataConfig().玩家数据加载前保持背包为空){
            event.getPlayer().getInventory().clear();
        }
        BukkitRunnable 加载 = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {
                PlayerDataLock lock = PlayerData.playerDataSupport.getPlayerDataLock(event.getPlayer());
                if (lock.isLocked()) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MyWorldBukkit.getLang().玩家数据加载_等待信息.replaceAll("<数>", String.valueOf(time))));
                    time++;
                    return;
                }
                lock.locked(MyWorldBukkit.getWorldConfig().服务器名称);
                PlayerData.playerDataSupport.loadData(event.getPlayer());
                playerLockMap.put(event.getPlayer(), lock);
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MyWorldBukkit.getLang().玩家数据加载_完成));
                //传送玩家到上次的非玩家世界位置
                if (MyWorldBukkit.getPlayerDataConfig().保存玩家非玩家世界位置){
                    Location location = 加载位置(event.getPlayer());
                    if (location!=null){
                        event.getPlayer().teleport(location);
                    }
                }
                this.cancel();
                playerLoadRunMap.remove(event.getPlayer());

                //加载完成任务
                Queue<Runnable> queue = playerLoadFinishedToRunMap.get(event.getPlayer());
                if (queue==null){
                    return;
                }
                while (true){
                    Runnable runnable = queue.poll();
                    if (runnable==null){
                        break;
                    }
                    runnable.run();
                }

            }
        };
        playerLoadRunMap.put(event.getPlayer(),加载);
        加载.runTaskTimer(MyWorldBukkit.getMyWorldBukkit(), 1, MyWorldBukkit.getPlayerDataConfig().玩家数据解锁检测间隙);

    }
    /**
     * 主要用于记录玩家最后的位置
     * 最后的玩家世界的位置
     * 和最后的非玩家世界的位置
     * */
    @EventHandler
    public void 玩家传送事件(PlayerTeleportEvent event){
        if (Objects.requireNonNull(event.getTo()).getWorld()==event.getFrom().getWorld()){//传送的是相同的世界就不管
            return;
        }
        PlayerWorlds playerWordFrom = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(Objects.requireNonNull(Objects.requireNonNull(event.getFrom()).getWorld()));
        PlayerWorlds playerWordTo = MyWorldBukkit.getPlayerWordMangaer().getBeLoadPlayerWorlds(Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()));
        if (playerWordFrom!=null){
            if (playerWordFrom!=playerWordTo){
                playerWordFrom.savePlayerLocation(event.getPlayer().getName(),event.getFrom());
                return;
            }
        }
        //记录玩家最后的非玩家世界位置。
        if (MyWorldBukkit.getPlayerDataConfig().保存玩家非玩家世界位置){
            if (playerWordFrom==null&playerWordTo!=null){//确定是从非玩家世界传送到玩家世界
                保存位置(event.getPlayer(),event.getFrom());
            }
        }

    }

    /**
     * 加载玩家在非玩家世界下线的位置
     * */
    private Location 加载位置(Player player){
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(new File(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"Data"),player.getName()+"location"));
        return yamlConfiguration.getLocation("location",null);
    }
    /**
     * 保存玩家在非玩家世界下线的位置
     * */
    private void 保存位置(Player player,Location location){
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("location",location);
        try {
            yamlConfiguration.save(new File(new File(MyWorldBukkit.getMyWorldBukkit().getDataFolder(),"Data"),player.getName()+"location"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void 玩家丢东西(PlayerDropItemEvent event){
        if (!playerLockMap.containsKey(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void 物品拾取事件(EntityPickupItemEvent event){
        LivingEntity 实体 = event.getEntity();
        if (!(实体 instanceof Player)){
            return;
        }
        if (!playerLockMap.containsKey(实体)){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void 对象或空气进行交互(PlayerInteractEvent event){
        if (!playerLockMap.containsKey(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    public void close() {
        new HashMap<>(playerLockMap).forEach((player, lockWork) -> {
            PlayerData.playerDataSupport.saveData(player);
            lockWork.unlock(MyWorldBukkit.getWorldConfig().服务器名称);
        });
    }
}
