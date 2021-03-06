package cn.jja8.myWorld.bukkit.listener;

import cn.jja8.myWorld.bukkit.ConfigBukkit;
import cn.jja8.myWorld.bukkit.MyWorldBukkit;
import cn.jja8.myWorld.bukkit.config.WorldConfig;
import cn.jja8.myWorld.bukkit.work.MyWorldManger;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGrouping;
import cn.jja8.myWorld.bukkit.work.MyWorldWorldGroupingWorlding;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于卸载不需要被继续加载的世界
 * */
public class WorldClean  implements Listener {
    WorldConfig worldConfig = ConfigBukkit.getWorldConfig();
    List<MyWorldWorldGrouping> 空世界 = new ArrayList<>();
    List<MyWorldWorldGrouping> 过期空世界 = new ArrayList<>();
    List<MyWorldWorldGrouping> cleaningWorlds = new ArrayList<>();
    public WorldClean(){
        MyWorldBukkit.getMyWorldBukkit().getServer().getPluginManager().registerEvents(this, MyWorldBukkit.getMyWorldBukkit());
        //扫描过期世界
        new BukkitRunnable(){
            @Override
            public void run() {
                cleaningWorlds.addAll(过期空世界);
                过期空世界 = 空世界;
                空世界 = new ArrayList<>();
                //扫描空世界
                List<MyWorldWorldGrouping> playerWorldsList = new ArrayList<>(MyWorldManger.getLoadedWorldGrouping().values());
                playerWorldsList.removeAll(空世界);
                playerWorldsList.removeAll(过期空世界);
                playerWorldsList.removeAll(cleaningWorlds);
                playerWorldsList.forEach(playerWord -> {
                    boolean k = true;
                    for (MyWorldWorldGroupingWorlding value : playerWord.getAllLoadWorld().values()) {
                        if (value.getMyWorldWorlding().getWorld().getPlayers().size()>0){
                            k = false;
                            break;
                        }
                    }
                    if (k){
                        空世界.add(playerWord);
                    }
                });
            }
        }.runTaskTimer(MyWorldBukkit.getMyWorldBukkit(), worldConfig.无玩家世界最短卸载时间*20, worldConfig.无玩家世界最短卸载时间*20);
        //卸载过期世界
        new BukkitRunnable(){
            @Override
            public void run() {
                if (cleaningWorlds.size()>0){
                    MyWorldWorldGrouping playerWorlds = cleaningWorlds.remove(0);
                    playerWorlds.unLoad(true);
                }
            }
        }.runTaskTimer(MyWorldBukkit.getMyWorldBukkit(), worldConfig.卸载空世界间隔时间*20, worldConfig.卸载空世界间隔时间*20);
    }

    @EventHandler
    public void 玩家传送(PlayerTeleportEvent event){
        Location lo = event.getTo();
        if (lo==null){
            return;
        }
        World word = lo.getWorld();
        if (word==null){
            return;
        }
        MyWorldWorldGrouping wo = MyWorldManger.getWorldGrouping(word);
        if (wo==null){
            return;
        }
        空世界.remove(wo);
        过期空世界.remove(wo);
        cleaningWorlds.remove(wo);
    }

    @EventHandler
    public void 玩家进入(PlayerJoinEvent event){
        World word = event.getPlayer().getLocation().getWorld();
        if (word==null){
            return;
        }
        MyWorldWorldGrouping wo = MyWorldManger.getWorldGrouping(word);
        if (wo==null){
            return;
        }
        空世界.remove(wo);
        过期空世界.remove(wo);
        cleaningWorlds.remove(wo);
    }

}
