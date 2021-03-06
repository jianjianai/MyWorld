package cn.jja8.myWorld.bukkit.listener;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.basic.PlayerData;
import cn.jja8.myWorld.bukkit.basic.playerDataSupport.PlayerDataLock;
import cn.jja8.myWorld.bukkit.config.Lang;
import cn.jja8.myWorld.bukkit.config.PlayerDataConfig;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * 主要负责管理玩家的数据加载和保存
 * 包括玩家背包，玩家下线时的位置。
 * ”玩家数据“字段负责玩家数据的存储，如果需要使用其他保存方式可以在load阶段对他赋值。
 * */
public class PlayerDataManager implements Listener {
    PlayerDataConfig playerDataConfig = ConfigBukkit.getPlayerDataConfig();
    WorldConfig worldConfig = ConfigBukkit.getWorldConfig();
    Lang lang = ConfigBukkit.getLang();
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
        MyWorldBukkit.getMyWorldBukkit().getServer().getScheduler().runTaskTimerAsynchronously(MyWorldBukkit.getMyWorldBukkit(), () -> new ArrayList<>(playerLockMap.values()).forEach(lock ->lock.saveData() ), 20L * playerDataConfig.自动保存时间, 20L * playerDataConfig.自动保存时间);
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
        if (lock!=null){
            lock.saveData();
            lock.unlock();
        }
    }
    @EventHandler
    public void 玩家进入服务器(PlayerJoinEvent event){
        if (playerDataConfig.玩家数据加载前保持背包为空){
            event.getPlayer().getInventory().clear();
        }
        BukkitRunnable 加载 = new BukkitRunnable() {
            int time = 0;
            @Override
            public void run() {
                PlayerDataLock lock = PlayerData.playerDataSupport.getPlayerDataLock(event.getPlayer(),worldConfig.服务器名称);
                if (lock==null) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lang.玩家数据加载_等待信息.replaceAll("<数>", String.valueOf(time))));
                    time++;
                    return;
                }
                playerLockMap.put(event.getPlayer(), lock);
                this.cancel();
                playerLoadRunMap.remove(event.getPlayer());
                lock.loadData();
                //加载完成任务
                Queue<Runnable> queue = playerLoadFinishedToRunMap.get(event.getPlayer());
                if (queue!=null){
                    while (true){
                        Runnable runnable = queue.poll();
                        if (runnable==null){
                            break;
                        }
                        runnable.run();
                    }
                }
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(lang.玩家数据加载_完成));
            }
        };
        playerLoadRunMap.put(event.getPlayer(),加载);
        加载.runTaskTimer(MyWorldBukkit.getMyWorldBukkit(), 1, playerDataConfig.玩家数据解锁检测间隙);

    }




    @EventHandler
    public void 玩家丢东西(PlayerDropItemEvent event){
        if (!playerLockMap.containsKey(event.getPlayer())){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void 物品拾取事件(EntityPickupItemEvent event){
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)){
            return;
        }
        if (!playerLockMap.containsKey(entity)){
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
            lockWork.saveData();
            lockWork.unlock();
        });
    }
}
